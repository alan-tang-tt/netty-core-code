package com.imooc.netty.mahjong.server.processor;

import com.imooc.netty.mahjong.common.domain.Player;
import com.imooc.netty.mahjong.common.domain.Room;
import com.imooc.netty.mahjong.common.proto.CreateRoomRequest;
import com.imooc.netty.mahjong.common.proto.CreateRoomResponse;
import com.imooc.netty.mahjong.common.proto.RoomRefreshNotification;
import com.imooc.netty.mahjong.common.proto.RoomStatus;
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
        room.setStatus(RoomStatus.STATUS_WAITING_PLAYER);
        MahjongContext.currentContext().setRoomById(room);

        // 玩家进入房间
        addPlayer(room, MahjongContext.currentContext().getCurrentPlayer());

        // 响应
        CreateRoomResponse response = CreateRoomResponse.newBuilder()
                .setResult(true)
                .setMessage("success")
                .build();
        MessageUtils.sendResponse(response);

        // 刷新房间信息
        RoomRefreshNotification refreshNotification = RoomRefreshNotification.newBuilder()
                .setRoom(room.toRoomMsg())
                .build();
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
