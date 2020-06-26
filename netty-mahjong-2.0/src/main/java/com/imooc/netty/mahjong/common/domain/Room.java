package com.imooc.netty.mahjong.common.domain;

import com.google.protobuf.ByteString;
import com.imooc.netty.mahjong.common.proto.RoomMsg;
import com.imooc.netty.mahjong.common.proto.RoomStatus;
import lombok.Data;

@Data
public class Room {
    /**
     * 房间id
     */
    private long id;
    /**
     * 房间最大的人数
     */
    private int maxPlayerNum;
    /**
     * 底分
     */
    private int baseScore;
    /**
     * 房间内的玩家列表
     */
    private Player[] players;
    /**
     * 未摸的牌
     */
    private byte[] remainCards;
    /**
     * 出牌玩家的位置
     */
    private int chuPos;
    /**
     * 状态
     */
    private RoomStatus status;

    public RoomMsg toRoomMsg() {
        RoomMsg.Builder builder = RoomMsg.newBuilder()
                .setId(id)
                .setMaxPlayerNum(maxPlayerNum)
                .setBaseScore(baseScore)
                .setChuPos(chuPos)
                .setStatus(status);

        if (players != null) {
            for (Player player : players) {
                if (player != null) {
                    builder.addPlayers(player.toPlayerMsg());
                }
            }
        }

        if (remainCards != null) {
            builder.setRemainCards(ByteString.copyFrom(remainCards));
        }

        return builder.build();
    }

}
