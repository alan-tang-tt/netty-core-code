package com.imooc.netty.mahjong.server.processor;

import com.imooc.netty.mahjong.common.domain.Player;
import com.imooc.netty.mahjong.common.domain.Room;
import com.imooc.netty.mahjong.common.proto.*;
import com.imooc.netty.mahjong.common.util.MahjongContext;
import com.imooc.netty.mahjong.common.util.MessageUtils;
import com.imooc.netty.mahjong.common.util.OperationUtils;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

@Slf4j
public class OperationRequestProcessor implements MahjongProcessor<OperationRequest> {
    @Override
    public void process(OperationRequest message) {
        // 获取当前房间信息
        Room room = MahjongContext.currentContext().getCurrentRoom();
        // 当前操作
        int operation = message.getOperation();

        // 检查
        if (room == null) {
            log.error("room not exist");
            return;
        }

        if (room.getStatus() != RoomStatus.STATUS_WAITING_CHU && room.getStatus() != RoomStatus.STATUS_WAITING_OPERATION) {
            log.error("room status error, roomStatus={}", room.getStatus());
            return;
        }

        if (room.getStatus() == RoomStatus.STATUS_WAITING_CHU && operation != OperationUtils.OPERATION_CHU) {
            log.error("operation request error, roomStatus={}, operation={}", room.getStatus(), operation);
            return;
        }

        if (room.getStatus() == RoomStatus.STATUS_WAITING_OPERATION && operation == OperationUtils.OPERATION_CHU) {
            log.error("operation request error, roomStatus={}, operation={}", room.getStatus(), operation);
            return;
        }

        if (operation == OperationUtils.OPERATION_CHU) {
            // 如果是出牌操作
            chu(room, message);
        } else {
            // 如果是其他操作，需等待所有可操作之人操作完成之后才能判断谁的操作是有效的
            operate(room, message);
        }
    }

    private void chu(Room room, OperationRequest message) {
        // 当前玩家
        Player player = MahjongContext.currentContext().getCurrentPlayer();
        // 检查是不是当前玩家出牌
        if (room.getChuPos() != player.getPos()) {
            log.error("not current player chu, roomChuPos={}, currentPlayerPos={}", room.getChuPos(), player.getPos());
            return;
        }

        byte chuCard = (byte) message.getCard();

        // 从玩家手中移除该牌
        if (!removeCard(player, chuCard)) {
            log.error("player has not that card, playerPos={}, chuCard={}", player.getPos(), chuCard);
            return;
        }

        // 添加到出牌列表中
        addToChuCards(player, chuCard);

        // 通知有人出了一张牌
        OperationResultNotification operationResultNotification = OperationResultNotification.newBuilder()
                .setOperation(OperationUtils.OPERATION_CHU)
                .setPos(player.getPos())
                .setCard(chuCard)
                .build();
        MessageUtils.sendNotification(room, operationResultNotification);

        // 刷新房间信息牌
        RoomRefreshNotification refreshNotification = RoomRefreshNotification.newBuilder()
                .setOperation(OperationUtils.OPERATION_CHU)
                .setRoom(room.toRoomMsg())
                .build();
        MessageUtils.sendRoomRefreshNotification(refreshNotification);

        // 检查其他玩家是否可以操作
        if (!checkOtherCanOperation(room, player, chuCard)) {
            // 如果其他玩家不可以操作，移动到下一个玩家摸牌
            moveToNextPlayer(room);
        }
    }

    private boolean removeCard(Player player, byte... removedCards) {
        byte[] cards = player.getCards();
        outer:
        for (byte removedCard : removedCards) {
            for (int i = 0; i < cards.length; i++) {
                if (removedCard == cards[i]) {
                    cards[i] = 0;
                    continue outer;
                }
            }
            return false;
        }
        return true;
    }

    private void addToChuCards(Player player, byte chuCard) {
        byte[] chuCards = player.getChuCards();
        if (chuCards == null) {
            chuCards = new byte[100];
            player.setChuCards(chuCards);
        }
        for (int i = 0; i < chuCards.length; i++) {
            if (chuCards[i] == 0) {
                chuCards[i] = chuCard;
                break;
            }
        }
    }

    private boolean checkOtherCanOperation(Room room, Player chuPlayer, byte chuCard) {
        List<OperationNotification> operationNotifications = new ArrayList<>();
        Player[] players = room.getPlayers();
        for (Player player : players) {
            if (player != null && player.getId() != chuPlayer.getId()) {
                int operation = 0;
                // 检查是否可碰
                if (containCard(player.getCards(), chuCard, chuCard)) {
                    operation |= OperationUtils.OPERATION_PENG;
                }

                // 检查是否可明杠
                if (containCard(player.getCards(), chuCard, chuCard, chuCard)) {
                    operation |= OperationUtils.OPERATION_GANG;
                }

                // 检查是否可胡
                if (checkHu(player, chuCard)) {
                    operation |= OperationUtils.OPERATION_HU;
                }

                if (operation != 0) {
                    operation |= OperationUtils.OPERATION_GUO;
                    OperationNotification operationNotification = OperationNotification.newBuilder()
                            .setOperation(operation)
                            .setPos(player.getPos())
                            .setFireCard(chuCard)
                            .build();
                    operationNotifications.add(operationNotification);
                }
            }
        }

        // 针对可以操作的玩家发送消息
        if (!operationNotifications.isEmpty()) {
            room.setStatus(RoomStatus.STATUS_WAITING_OPERATION);

            List<Player> exceptPlayers = new ArrayList<>();
            for (OperationNotification operationNotification : operationNotifications) {
                Player player = players[operationNotification.getPos()];
                exceptPlayers.add(player);
                MessageUtils.sendOperationNotification(player, operationNotification);
            }

            // 针对其他玩家发送等待操作的通知
            // 其他玩家既不知道在等谁操作，也不知道等待的什么操作
            OperationNotification operationNotification = OperationNotification
                    .newBuilder()
                    .setFireCard(chuCard)
                    .setPos(-1)
                    .build();
            MessageUtils.sendOperationNotification(room, operationNotification, exceptPlayers);

            // 添加房间等待的操作
            MahjongContext.currentContext().setRoomWaitingOperations(room, operationNotifications);
            return true;
        }
        return false;
    }

    private void moveToNextPlayer(Room room) {
        // 出牌位置移动到下一个玩家
        int nextPos = (room.getChuPos() + 1) % room.getMaxPlayerNum();
        room.setChuPos(nextPos);
        Player nextPlayer = room.getPlayers()[nextPos];

        // 摸牌
        byte grabCard = popCard(room);
        grabCard(room, nextPlayer, grabCard);
    }

    private void grabCard(Room room, Player player, byte grabCard) {
        // 摸牌
        if (grabCard == 0) {
            gameOver(room, null);
            return;
        }
        addCard(player, grabCard);

        // 通知有人摸了一张牌
        OperationResultNotification operationResultNotification = OperationResultNotification
                .newBuilder()
                .setOperation(OperationUtils.OPERATION_GRAB)
                .setPos(player.getPos())
                .build();
        MessageUtils.sendNotification(room, operationResultNotification);

        // 刷新房间信息
        RoomRefreshNotification refreshNotification = RoomRefreshNotification
                .newBuilder()
                .setOperation(OperationUtils.OPERATION_GRAB)
                .setRoom(room.toRoomMsg()).build();
        MessageUtils.sendRoomRefreshNotification(refreshNotification);

        int operation = 0;
        // 判断是否可以补杠，规则是手里有一张牌，在碰的列表里（不仅限于刚摸的这张牌）
        // 判断是否可以暗杠，规则是手里有四张一样的牌（不仅限于刚摸的这张牌）
        if (checkBuGang(player, grabCard) || checkAnGang(player, grabCard)) {
            operation |= OperationUtils.OPERATION_GANG;
        }

        // 判断是否可以胡牌，规则保密
        if (checkHu(player)) {
            operation |= OperationUtils.OPERATION_HU;
        }

        List<Player> exceptPlayers;
        if (operation != 0) {
            operation |= OperationUtils.OPERATION_GUO;

            room.setStatus(RoomStatus.STATUS_WAITING_OPERATION);
            // 通知摸牌的玩家可操作
            OperationNotification operationNotification = OperationNotification
                    .newBuilder()
                    .setOperation(operation)
                    .setPos(player.getPos())
                    .setFireCard(grabCard)
                    .build();
            MessageUtils.sendOperationNotification(player, operationNotification);

            // 记录下来当前房间等待的操作，方便后面的请求过来的时候做验证
            MahjongContext.currentContext().setRoomWaitingOperations(room, Collections.singletonList(operationNotification));

            exceptPlayers = Collections.singletonList(player);
        } else {
            // 没有可操作，通知其出牌
            exceptPlayers = Collections.emptyList();

            room.setStatus(RoomStatus.STATUS_WAITING_CHU);
        }

        // 通知等待出牌（不管是否有其他操作都通知其他玩家等待出牌）
        OperationNotification operationNotification = OperationNotification
                .newBuilder()
                .setPos(player.getPos())
                .setOperation(OperationUtils.OPERATION_CHU)
                .build();
        MessageUtils.sendOperationNotification(room, operationNotification, exceptPlayers);
    }

    private boolean checkBuGang(Player player, byte card) {
        // 实际应针对所有手牌检测是否可以补杠
        // 我们这里简单点，只针对刚摸的牌
        byte[] pengList = player.getPengList();
        if (pengList != null) {
            for (byte p : pengList) {
                if (p == card) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkAnGang(Player player, byte card) {
        // 实际应针对所有手牌检测是否可以暗杠
        // 我们这里简单点，只针对刚摸的牌
        return containCard(player.getCards(), card, card, card, card);
    }

    private boolean containCard(byte[] sourceCards, byte... cards) {
        // 克隆一份，不影响原数据
        byte[] cloneCards = sourceCards.clone();
        outer:
        for (byte card : cards) {
            for (int i = 0; i < cloneCards.length; i++) {
                if (cloneCards[i] == card) {
                    // 找到了就归0，不影响多张一样牌的情况
                    cloneCards[i] = 0;
                    continue outer;
                }
            }
            return false;
        }
        return true;
    }

    private boolean checkHu(Player player) {
        // 胡牌一般分成两种：七对和3*N+2
        // 胡牌算法是麻将的核心算法，对于非业内人士都是保密的
        // 我们这里使用随机数模拟
        if (ThreadLocalRandom.current().nextInt(5) == 1) {
            return true;
        }
        return false;
    }

    private boolean checkHu(Player player, byte chuCard) {
        // 胡牌一般分成两种：七对和3*N+2
        // 胡牌算法是麻将的核心算法，对于非业内人士都是保密的
        // 我们这里使用随机数模拟
        if (ThreadLocalRandom.current().nextInt(5) == 1) {
            return true;
        }
        return false;
    }

    private void addCard(Player player, byte card) {
        byte[] cards = player.getCards();
        for (int i = 0; i < cards.length; i++) {
            if (cards[i] == 0) {
                cards[i] = card;
                break;
            }
        }
    }

    private void gameOver(Room room, Player winner) {
        // 游戏结束
        room.setStatus(RoomStatus.STATUS_GAME_OVER);
        // 发送通知
        GameOverNotification gameOverNotification = GameOverNotification
                .newBuilder()
                .setRoom(room.toRoomMsg())
                .build();
        MessageUtils.sendNotification(room, gameOverNotification);

        // 计算输赢
        List<Integer> scores = new ArrayList<>(room.getMaxPlayerNum());
        if (winner != null) {
            for (int i = 0; i < room.getMaxPlayerNum(); i++) {
                Player player = room.getPlayers()[i];
                if (i == winner.getPos()) {
                    int winScore = room.getBaseScore() * (room.getMaxPlayerNum() - 1);
                    scores.add(winScore);
                    player.setScore(player.getScore() + winScore);
                } else {
                    int loseScore = -room.getBaseScore();
                    scores.add(loseScore);
                    player.setScore(player.getScore() + loseScore);
                }
            }
        }

        // 发送结算通知
        SettlementNotification settlementNotification = SettlementNotification
                .newBuilder()
                .addAllScores(scores)
                .build();
        MessageUtils.sendNotification(room, settlementNotification);

        // 清理资源
        MahjongContext mahjongContext = MahjongContext.currentContext();
        mahjongContext.removeRoomById(room.getId());
        for (Player player : room.getPlayers()) {
            player.setPos(-1);
            player.setCards(null);
            player.setChuCards(null);
            player.setPengList(null);
            player.setGangList(null);

            Channel channel = mahjongContext.getPlayerIdChannel(player.getId());
            mahjongContext.removeChannelRoomId(channel);
        }
        room.setChuPos(-1);
        room.setRemainCards(null);
        room.setPlayers(null);
    }

    private byte popCard(Room room) {
        byte[] remainCards = room.getRemainCards();
        for (int i = 0; i < remainCards.length; i++) {
            if (remainCards[i] != 0) {
                byte card = remainCards[i];
                remainCards[i] = 0;
                return card;
            }
        }
        return 0;
    }

    private byte popLastCard(Room room) {
        byte[] remainCards = room.getRemainCards();
        for (int i = remainCards.length - 1; i >= 0; i--) {
            if (remainCards[i] != 0) {
                byte card = remainCards[i];
                remainCards[i] = 0;
                return card;
            }
        }
        return 0;
    }

    private void operate(Room room, OperationRequest message) {
        // 实际上，操作是有优先级的，比如，你打了一张牌，三家可胡，如果下家直接选择胡，则不用等待其他玩家操作了
        // 再比如，你打了一张牌，一家可碰，一家可胡，则有人选择胡了，也不用再等另一家操作了
        // 我们这里简单点，等所有玩家操作完了再判断
        Player player = MahjongContext.currentContext().getCurrentPlayer();

        // 检查当前玩家是否有操作的权限
        List<OperationNotification> waitingOperations = MahjongContext.currentContext().getRoomWaitingOperations(room);
        if (!checkAllowed(player, message, waitingOperations)) {
            log.error("player operation not allowed, playerPos={}, operation={}", player.getPos(), message.getOperation());
            return;
        }

        // 检查是否重复操作
        List<OperationRequest> roomOperationRequests = MahjongContext.currentContext().getRoomOperationRequests(room);
        if (checkDuplicated(player, roomOperationRequests)) {
            log.error("player operation duplicated, playerPos={}, operation={}", player.getPos(), message.getOperation());
            return;
        }

        // 添加到等待队列中
        if (roomOperationRequests == null) {
            roomOperationRequests = new ArrayList<>();
            MahjongContext.currentContext().setRoomOperationRequests(room, roomOperationRequests);
        }
        roomOperationRequests.add(message);

        // 如果所有玩家都操作了，则判断谁的操作有效
        if (waitingOperations.size() == roomOperationRequests.size()) {
            // 清除这两个list
            MahjongContext.currentContext().removeRoomOperationRequests(room);
            MahjongContext.currentContext().removeRoomWaitingOperations(room);

            // 检查胡（胡是可能有多个的，要考虑最近的）
            if (hasHu(room, roomOperationRequests)) {
                return;
            }

            // 检查杠
            if (hasGang(room, roomOperationRequests)) {
                return;
            }

            // 检查碰
            if (hasPeng(room, roomOperationRequests)) {
                return;
            }

            // 以上都没有，就是过，移动到下一个玩家摸牌
            // 如果是自己摸牌，提示操作，这时候过了，应该提示他出牌
            Player chuPlayer = room.getPlayers()[room.getChuPos()];
            if (validCardNum(chuPlayer) % 3 == 2) {
                room.setStatus(RoomStatus.STATUS_WAITING_CHU);

                OperationNotification operationNotification = OperationNotification.newBuilder()
                        .setOperation(OperationUtils.OPERATION_CHU)
                        .setPos(room.getChuPos())
                        .build();
                MessageUtils.sendOperationNotification(chuPlayer, operationNotification);
            } else {
                moveToNextPlayer(room);
            }
        }
    }

    private int validCardNum(Player player) {
        int num = 0;
        byte[] cards = player.getCards();
        for (int i = 0; i < cards.length; i++) {
            if (cards[i] != 0) {
                num++;
            }
        }
        return num;
    }

    private boolean hasHu(Room room, List<OperationRequest> roomOperationRequests) {
        // 把list转换成数组，按位置摆放
        OperationRequest[] operationRequestArray = new OperationRequest[room.getMaxPlayerNum()];
        for (OperationRequest operationRequest : roomOperationRequests) {
            operationRequestArray[operationRequest.getPos()] = operationRequest;
        }

        // 从当前出牌玩家向后遍历
        int pos = room.getChuPos();
        for (; ; ) {
            OperationRequest operationRequest = operationRequestArray[pos];
            if (operationRequest != null && operationRequest.getOperation() == OperationUtils.OPERATION_HU) {
                Player winner = room.getPlayers()[pos];
                addCard(winner, (byte) operationRequest.getCard());
                gameOver(room, winner);
                return true;
            }
            pos = ++pos % room.getMaxPlayerNum();
            if (pos == room.getChuPos()) {
                break;
            }
        }
        return false;
    }

    private boolean hasGang(Room room, List<OperationRequest> roomOperationRequests) {
        for (OperationRequest operationRequest : roomOperationRequests) {
            if (operationRequest.getOperation() == OperationUtils.OPERATION_GANG) {
                Player player = room.getPlayers()[operationRequest.getPos()];
                byte opCard = (byte) operationRequest.getCard();

                // 补杠、明杠、暗杠都减4张牌，不检查是否成功
                removeCard(player, opCard, opCard, opCard, opCard);

                // 杠牌加一张牌
                addGang(player, opCard);

                // 补杠碰牌减一张
                removePeng(player, opCard);

                // 通知有人杠了
                OperationResultNotification operationResultNotification = OperationResultNotification.newBuilder()
                        .setPos(operationRequest.getPos())
                        .setOperation(operationRequest.getOperation())
                        .setCard(opCard)
                        .build();
                MessageUtils.sendNotification(room, operationResultNotification);

                // 出牌位置移动到杠的玩家
                room.setChuPos(player.getPos());
                room.setStatus(RoomStatus.STATUS_WAITING_CHU);

                // 刷新房间信息
                RoomRefreshNotification refreshNotification = RoomRefreshNotification
                        .newBuilder().setOperation(OperationUtils.OPERATION_GANG)
                        .setRoom(room.toRoomMsg())
                        .build();
                MessageUtils.sendRoomRefreshNotification(refreshNotification);

                // 从牌尾摸一张牌
                byte grabCard = popLastCard(room);
                grabCard(room, player, grabCard);

                return true;
            }
        }
        return false;
    }

    private void removePeng(Player player, byte card) {
        byte[] pengList = player.getPengList();
        if (pengList != null) {
            for (int i = 0; i < pengList.length; i++) {
                if (pengList[i] == card) {
                    pengList[i] = 0;
                    break;
                }
            }
        }
    }

    private void addGang(Player player, byte card) {
        byte[] gangList = player.getGangList();
        if (gangList == null) {
            gangList = new byte[27];
            player.setGangList(gangList);
        }
        for (int i = 0; i < gangList.length; i++) {
            if (gangList[i] == 0) {
                gangList[i] = card;
                break;
            }
        }
    }

    private boolean hasPeng(Room room, List<OperationRequest> roomOperationRequests) {
        for (OperationRequest operationRequest : roomOperationRequests) {
            if (operationRequest.getOperation() == OperationUtils.OPERATION_PENG) {
                Player player = room.getPlayers()[operationRequest.getPos()];
                // 手里去除这两张牌
                byte card = (byte) operationRequest.getCard();
                if (!removeCard(player, card, card)) {
                    log.error("peng error, player has not enough cards, card={}", card);
                    return false;
                }
                // 碰牌加一张牌
                addPeng(player, card);

                // 通知有人碰了
                OperationResultNotification operationResultNotification = OperationResultNotification
                        .newBuilder()
                        .setPos(operationRequest.getPos())
                        .setOperation(operationRequest.getOperation())
                        .setCard(card)
                        .build();
                MessageUtils.sendNotification(room, operationResultNotification);

                // 出牌位置移动到碰的玩家
                room.setChuPos(player.getPos());
                room.setStatus(RoomStatus.STATUS_WAITING_CHU);

                // 刷新房间信息
                RoomRefreshNotification refreshNotification = RoomRefreshNotification.newBuilder()
                        .setOperation(OperationUtils.OPERATION_PENG)
                        .setRoom(room.toRoomMsg())
                        .build();
                MessageUtils.sendRoomRefreshNotification(refreshNotification);

                // 通知出牌
                OperationNotification operationNotification = OperationNotification.newBuilder()
                        .setOperation(OperationUtils.OPERATION_CHU)
                        .setPos(player.getPos())
                        .build();
                MessageUtils.sendOperationNotification(room, operationNotification, Collections.emptyList());

                return true;
            }
        }
        return false;
    }

    private void addPeng(Player player, byte card) {
        byte[] pengList = player.getPengList();
        if (pengList == null) {
            pengList = new byte[27];
            player.setPengList(pengList);
        }
        for (int i = 0; i < pengList.length; i++) {
            if (pengList[i] == 0) {
                pengList[i] = card;
                break;
            }
        }
    }

    private boolean checkDuplicated(Player player, List<OperationRequest> roomOperationRequests) {
        if (roomOperationRequests != null && !roomOperationRequests.isEmpty()) {
            for (OperationRequest roomOperationRequest : roomOperationRequests) {
                if (roomOperationRequest.getPos() == player.getPos()) {
                    return true;
                }
            }
        }
        return false;
    }

    private boolean checkAllowed(Player player, OperationRequest message, List<OperationNotification> waitingOperations) {
        if (waitingOperations == null || waitingOperations.isEmpty()) {
            return false;
        }
        for (OperationNotification waitingOperation : waitingOperations) {
            if (waitingOperation.getPos() == player.getPos() && containOperation(waitingOperation.getOperation(), message.getOperation())) {
                return true;
            }
        }
        return false;
    }

    private boolean containOperation(int operations, int op) {
        return (operations & op) == op;
    }
}
