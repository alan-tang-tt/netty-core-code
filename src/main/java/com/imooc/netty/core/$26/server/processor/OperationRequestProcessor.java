package com.imooc.netty.core.$26.server.processor;

import com.imooc.netty.core.$26.common.domain.Player;
import com.imooc.netty.core.$26.common.domain.Table;
import com.imooc.netty.core.$26.common.msg.OperationNotification;
import com.imooc.netty.core.$26.common.msg.OperationRequest;
import com.imooc.netty.core.$26.common.msg.OperationResultNotification;
import com.imooc.netty.core.$26.server.data.DataManager;
import com.imooc.netty.core.$26.util.IdUtils;
import com.imooc.netty.core.$26.util.MsgUtils;
import com.imooc.netty.core.$26.util.OperationUtils;
import io.netty.channel.Channel;
import io.netty.util.concurrent.EventExecutor;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Slf4j
public class OperationRequestProcessor implements MahjongProcessor<OperationRequest> {

    @Override
    public void process(OperationRequest msg) {
        Long tableId = DataManager.CURRENT_TABLE_ID.get();
        Table table = DataManager.getTableById(tableId);
        if (table == null) {
            return;
        }
        // 检查序列号是否一致，不一致表示过时的消息，直接丢弃
        if (msg.getSequence() != table.getSequence()) {
            return;
        }
        // 检查桌子状态对不对
        int subStatus = table.getSubStatus();
        if (subStatus == Table.SUBSTATUS_WAITING_CHU && msg.getOperation() != OperationUtils.OPERATION_CHU) {
            return;
        }
        if (subStatus == Table.SUBSTATUS_WAITING_OPERATE && msg.getOperation() == OperationUtils.OPERATION_CHU) {
            return;
        }

        table.incrementSequence();

        // 这里也可以使用策略模式优化，不过操作也不多，不优化问题也不大
        switch (msg.getOperation()) {
            case OperationUtils.OPERATION_CHU:
                // 如果是出牌
                chu(table, msg);
                break;
            case OperationUtils.OPERATION_CHI:
                // 如果是吃
                chi(table, msg);
                break;
            case OperationUtils.OPERATION_PENG:
                // 如果是碰
                peng(table, msg);
                break;
            case OperationUtils.OPERATION_GANG:
                // 如果是杠
                gang(table, msg);
                break;
            case OperationUtils.OPERATION_HU:
                // 如果是胡
                hu(table, msg);
                break;
            default:
                log.error("error msg, msg={}", msg);
        }
    }

    private void chu(Table table, OperationRequest msg) {
        // 检查是不是轮到该玩家操作
        // channel为空表示是超时自动操作
        Channel channel = DataManager.CURRENT_CHANNEL.get();
        Player chuPlayer = table.chuPlayer();
        if (channel != null && chuPlayer.getId() != DataManager.currentPlayer().getId()) {
            return;
        }

        // 检查出牌是否正确
        if (msg.getCards().length != 1) {
            log.error("chu card must be length 1, playerId={}", chuPlayer.getId());
            return;
        }
        if (!chuPlayer.containCards(msg.getCards())) {
            log.error("chu card not found in player, playerId={}, chuCard={}", chuPlayer.getId(), msg.getCards());
            return;
        }

        // 出牌，即把这张牌从玩家手里移除
        byte chuCard = msg.getCards()[0];
        chuPlayer.removeCard(chuCard);

        // 通知所有玩家谁出了一张什么牌
        OperationResultNotification operationResultNotification = new OperationResultNotification();
        operationResultNotification.setOperation(OperationUtils.OPERATION_CHU);
        operationResultNotification.setOperationPos(table.getChuPos());
        operationResultNotification.setCards(new byte[]{chuCard});
        MsgUtils.send2Table(table, operationResultNotification);

        // 检查其它玩家有没有可以吃碰杠胡这张牌，并通知对应的玩家
        if (checkOtherCanOperate(chuPlayer, chuCard, table)) {
            // 设置倒计时，倒计时结束后还没有人操作，则进行下一步操作
            final int preSequence = table.getSequence();
            EventExecutor executor = DataManager.CURRENT_EXECUTOR.get();
            executor.schedule(()->{
                // 不一致，说明有人操作了，丢弃此消息
                if (preSequence == table.getSequence()) {
                    moveToNext(table);
                }
            }, OperationUtils.OPERATION_DEPLAY_TIME, TimeUnit.SECONDS);
        } else {
            // 如果所有玩家都没有操作，则把光标移到下一个玩家，让其摸牌，并通知其出牌，并启动倒计时
            moveToNext(table);
        }
    }

    private void moveToNext(Table table) {
        // 出牌光标移到下一个玩家
        table.incrementSequence();
        table.moveToNext();
        // 下一个玩家摸一张牌，并通知他
        Byte card = DataManager.popCard(table.getId());
        if (card == null) {
            // 牌摸完了，通知所有玩家游戏结束，并清理数据

            return;
        }



        // 通知其出牌，并启动倒计时


    }

    private boolean checkOtherCanOperate(Player chuPlayer, byte chuCard, Table table) {
        // 如果有可以操作的玩家，通知所有玩家等待，并启动倒计时
        Player[] players = table.getPlayers();
        List<OperationNotification> notificationList = new ArrayList<>();
        for (int i =0; i < players.length; i++) {
            Player player = players[i];
            if (player != null && player.getId() != chuPlayer.getId()) {
                int operation = 0;
                byte[] cards = player.getCards();
                // 检查碰
                if (player.containCards(chuCard, chuCard)) {
                    operation |= OperationUtils.OPERATION_PENG;
                }

                // 检查杠
                if (player.containCards(chuCard, chuCard, chuCard)) {
                    operation |= OperationUtils.OPERATION_GANG;
                }

                // 检查胡（规则复杂，不便于公开，这里使用随机判断是否可胡）
                if (IdUtils.randomInt(10) == 1) {
                    operation |= OperationUtils.OPERATION_HU;
                }

                OperationNotification notification = new OperationNotification();
                notification.setSequence(table.getSequence());
                notification.setOperation(operation);
                notification.setOperationPos(i);
                notification.setDelayTime(OperationUtils.OPERATION_DEPLAY_TIME);

                notificationList.add(notification);
            }
        }

        if (!notificationList.isEmpty()) {
            for (OperationNotification notification : notificationList) {
                MsgUtils.send2Player(players[notification.getOperationPos()], notification);
            }
            // 针对全体玩家，再发送一个等待的消息，让没收到可操作消息的玩家知道要等待
            OperationNotification notification = new OperationNotification();
            notification.setDelayTime(OperationUtils.OPERATION_DEPLAY_TIME);
            // 其它字段没有意义
            MsgUtils.send2Table(table, notification);
        }

        return !notificationList.isEmpty();
    }

    private void chi(Table table, OperationRequest msg) {
        throw new UnsupportedOperationException("chi not support");
    }

    private void peng(Table table, OperationRequest msg) {

    }

    private void gang(Table table, OperationRequest msg) {

    }

    private void hu(Table table, OperationRequest msg) {

    }
}
