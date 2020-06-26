package com.imooc.netty.mahjong.server.processor;

import com.imooc.netty.mahjong.common.domain.Player;
import com.imooc.netty.mahjong.common.domain.Room;
import com.imooc.netty.mahjong.common.msg.EnterRoomRequest;
import com.imooc.netty.mahjong.common.msg.EnterRoomResponse;
import com.imooc.netty.mahjong.common.msg.RoomRefreshNotification;
import com.imooc.netty.mahjong.common.msg.StartGameMessage;
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
            EnterRoomResponse response = new EnterRoomResponse();
            response.setResult(false);
            response.setMessage("room not exist");
            MessageUtils.sendResponse(response);
            return;
        }
        // 房间状态不是等待玩家
        if (room.getStatus() != Room.STATUS_WAITING_PLAYER) {
            EnterRoomResponse response = new EnterRoomResponse();
            response.setResult(false);
            response.setMessage("room status error");
            MessageUtils.sendResponse(response);
            return;
        }
        // 房间人已满
        if (room.getMaxPlayerNum() <= validPlayerNum(room)) {
            EnterRoomResponse response = new EnterRoomResponse();
            response.setResult(false);
            response.setMessage("room is full");
            MessageUtils.sendResponse(response);
            return;
        }

        // 加入房间
        addPlayer(room, MahjongContext.currentContext().getCurrentPlayer());

        // 响应
        EnterRoomResponse response = new EnterRoomResponse();
        response.setResult(true);
        response.setMessage("success");
        MessageUtils.sendResponse(response);

        // 通知所有玩家有新玩家加入
        RoomRefreshNotification notification = new RoomRefreshNotification();
        notification.setRoom(room);
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
        StartGameMessage startGameMessage = new StartGameMessage();
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
