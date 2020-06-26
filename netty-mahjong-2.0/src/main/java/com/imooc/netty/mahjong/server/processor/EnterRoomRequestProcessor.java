package com.imooc.netty.mahjong.server.processor;

import com.imooc.netty.mahjong.common.domain.Player;
import com.imooc.netty.mahjong.common.domain.Room;
import com.imooc.netty.mahjong.common.proto.*;
import com.imooc.netty.mahjong.common.protocol.MahjongProtocol;
import com.imooc.netty.mahjong.common.util.MahjongContext;
import com.imooc.netty.mahjong.common.util.MessageUtils;
import com.imooc.netty.mahjong.server.executor.MahjongEventExecutorGroup;

public class EnterRoomRequestProcessor implements MahjongProcessor<EnterRoomRequest> {
    @Override
    public void process(EnterRoomRequest message) {
        // 检查房间信息
        Room room = MahjongContext.currentContext().getRoomById(message.getRoomId());
        // 房间不存在
        if (room == null) {
            EnterRoomResponse response = EnterRoomResponse.newBuilder()
                    .setResult(false)
                    .setMessage("room not exist")
                    .build();
            MessageUtils.sendResponse(response);
            return;
        }
        // 房间状态不是等待玩家
        if (room.getStatus() != RoomStatus.STATUS_WAITING_PLAYER) {
            EnterRoomResponse response = EnterRoomResponse
                    .newBuilder()
                    .setResult(false)
                    .setMessage("room status error")
                    .build();
            MessageUtils.sendResponse(response);
            return;
        }
        // 房间人已满
        if (room.getMaxPlayerNum() <= validPlayerNum(room)) {
            EnterRoomResponse response = EnterRoomResponse
                    .newBuilder()
                    .setResult(false)
                    .setMessage("room is full")
                    .build();
            MessageUtils.sendResponse(response);
            return;
        }

        // 加入房间
        addPlayer(room, MahjongContext.currentContext().getCurrentPlayer());

        // 响应
        EnterRoomResponse response = EnterRoomResponse
                .newBuilder()
                .setResult(true)
                .setMessage("success")
                .build();
        MessageUtils.sendResponse(response);

        // 通知所有玩家有新玩家加入
        RoomRefreshNotification notification = RoomRefreshNotification
                .newBuilder()
                .setRoom(room.toRoomMsg())
                .build();
        MessageUtils.sendRoomRefreshNotification(notification);

        // 如果达到最大人数，直接开始游戏
        if (validPlayerNum(room) >= room.getMaxPlayerNum()) {
            startGame();
        }

    }

    private int validPlayerNum(Room room) {
        int num = 0;
        for (Player player : room.getPlayers()) {
            if (player != null) {
                num ++;
            }
        }
        return num;
    }

    private void startGame() {
        // 开始游戏的方法放到加入房间的处理器里明显是不合适的
        // 比较好的做法是构造一个开始游戏的消息
        // 把它扔到业务线程池中处理
        // 当然，同时还要创建一个开始游戏的处理器
        StartGameMessage startGameMessage = StartGameMessage.newBuilder().build();
        MahjongProtocol mahjongProtocol = new MahjongProtocol();
        mahjongProtocol.setHeader(MahjongContext.currentContext().getRequestHeader());
        mahjongProtocol.setBody(startGameMessage);
        MahjongEventExecutorGroup.execute(MahjongContext.currentContext().getCurrentChannel(), mahjongProtocol);
    }

    private void addPlayer(Room room, Player player) {
        Player[] players = room.getPlayers();
        for (int i = 0; i < players.length; i++) {
            if (players[i] == null) {
                players[i] = player;
                player.setPos(i);
                break;
            }
        }
    }
}
