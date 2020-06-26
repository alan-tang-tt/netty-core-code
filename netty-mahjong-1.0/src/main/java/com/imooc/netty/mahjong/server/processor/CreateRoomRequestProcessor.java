package com.imooc.netty.mahjong.server.processor;

import com.imooc.netty.mahjong.common.domain.Player;
import com.imooc.netty.mahjong.common.domain.Room;
import com.imooc.netty.mahjong.common.msg.CreateRoomRequest;
import com.imooc.netty.mahjong.common.msg.CreateRoomResponse;
import com.imooc.netty.mahjong.common.msg.RoomRefreshNotification;
import com.imooc.netty.mahjong.common.util.MahjongContext;
import com.imooc.netty.mahjong.common.util.MessageUtils;

public class CreateRoomRequestProcessor implements MahjongProcessor<CreateRoomRequest> {
    @Override
    public void process(CreateRoomRequest message) {
        // 创建一个房间并加载到线程本地缓存中
        Room room = new Room();
        room.setId(MahjongContext.currentContext().getCurrentRoomId());
        room.setMaxPlayerNum(message.getPlayerNum());
        room.setBaseScore(message.getBaseScore());
        room.setPlayers(new Player[message.getPlayerNum()]);
        room.setStatus(Room.STATUS_WAITING_PLAYER);
        MahjongContext.currentContext().setRoomById(room);

        // 玩家进入房间
        addPlayer(room, MahjongContext.currentContext().getCurrentPlayer());

        // 响应
        CreateRoomResponse response = new CreateRoomResponse();
        response.setResult(true);
        response.setMessage("success");
        MessageUtils.sendResponse(response);

        // 刷新房间信息
        RoomRefreshNotification refreshNotification = new RoomRefreshNotification();
        refreshNotification.setRoom(room);
        MessageUtils.sendRoomRefreshNotification(refreshNotification);
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
