package com.imooc.netty.mahjong.common.domain;

import lombok.Data;

@Data
public class Room {
    /**
     * 等待其他玩家进入房间
     */
    public static final int STATUS_WAITING_PLAYER = 1;
    /**
     * 游戏开始
     */
    public static final int STATUS_GAME_STARTING = 2;
    /**
     * 等待玩家出牌
     */
    public static final int STATUS_WAITING_CHU = 3;
    /**
     * 等待其他玩家操作（碰、杠、胡）
     */
    public static final int STATUS_WAITING_OPERATION = 4;
    /**
     * 游戏结束
     */
    public static final int STATUS_GAME_OVER = 5;

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
    private int status;
}
