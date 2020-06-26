package com.imooc.netty.mahjong.common.domain;

import lombok.Data;

@Data
public class Player {
    /**
     * 唯一标识id
     */
    private long id;
    /**
     * 用户名
     */
    private String username;
    /**
     * 密码
     */
    private String password;
    /**
     * 玩家的积分
     */
    private int score;

    /**
     * 玩家在桌子上的位置
     */
    private int pos;
    /**
     * 手牌列表
     */
    private byte[] cards;
    /**
     * 打出的牌
     */
    private byte[] chuCards;
    /**
     * 碰的牌
     */
    private byte[] pengList;
    /**
     * 杠的牌
     */
    private byte[] gangList;
}
