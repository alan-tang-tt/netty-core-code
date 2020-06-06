package com.imooc.netty.core.$26.common.domain;

import io.netty.channel.Channel;
import lombok.Data;

@Data
public class Player {
    /**
     * 玩家id
     */
    private long id;
    /**
     * 玩家昵称
     */
    private String name;
    /**
     * 玩家积分
     */
    private int score;
    /**
     * 玩家钻石
     */
    private int diamond;
    /**
     * 玩家拥有的牌
     */
    private byte[] cards;

}
