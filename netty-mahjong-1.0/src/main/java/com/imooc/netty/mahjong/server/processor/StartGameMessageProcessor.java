package com.imooc.netty.mahjong.server.processor;

import com.imooc.netty.mahjong.common.domain.Player;
import com.imooc.netty.mahjong.common.domain.Room;
import com.imooc.netty.mahjong.common.msg.OperationNotification;
import com.imooc.netty.mahjong.common.msg.RoomRefreshNotification;
import com.imooc.netty.mahjong.common.msg.StartGameMessage;
import com.imooc.netty.mahjong.common.util.CardUtils;
import com.imooc.netty.mahjong.common.util.MahjongContext;
import com.imooc.netty.mahjong.common.util.MessageUtils;
import com.imooc.netty.mahjong.common.util.OperationUtils;

import java.util.Collections;

public class StartGameMessageProcessor implements MahjongProcessor<StartGameMessage> {
    @Override
    public void process(StartGameMessage message) {
        // 游戏即将开始
        Room room = MahjongContext.currentContext().getCurrentRoom();
        room.setStatus(Room.STATUS_GAME_STARTING);
        RoomRefreshNotification notification = new RoomRefreshNotification();
        notification.setRoom(room);
        MessageUtils.sendRoomRefreshNotification(notification);

        // 初始牌，并洗牌
        byte[] cards = CardUtils.initCard();
        room.setRemainCards(cards);

        // 发牌，庄家14张，闲家13张，默认0号位为庄家
        int zhuangPos = 0;
        Player[] players = room.getPlayers();
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            if (i == zhuangPos) {
                player.setCards(popCards(room, 14));
            } else {
                player.setCards(popCards(room, 13));
            }
        }

        // 通知发牌完毕，即刷新房间通知
        room.setChuPos(zhuangPos);
        room.setStatus(Room.STATUS_WAITING_CHU);
        MessageUtils.sendRoomRefreshNotification(notification);

        // 通知所有玩家等待庄家出牌
        OperationNotification operationNotification = new OperationNotification();
        operationNotification.setPos(zhuangPos);
        operationNotification.setOperation(OperationUtils.OPERATION_CHU);
        MessageUtils.sendOperationNotification(room, operationNotification, Collections.emptyList());
    }

    private byte[] popCards(Room room, int count) {
        byte[] remainCards = room.getRemainCards();
        byte[] cards = new byte[14];
        int num = 0;
        for (int i = 0; i < remainCards.length; i++) {
            if (remainCards[i] != 0) {
                cards[num++] = remainCards[i];
                remainCards[i] = 0;
                if (num == count) {
                    break;
                }
            }
        }
        return cards;
    }
}
