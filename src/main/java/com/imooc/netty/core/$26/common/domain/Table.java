package com.imooc.netty.core.$26.common.domain;

import lombok.Data;

@Data
public class Table {
    /**
     * 桌子id
     */
    private long id;
    /**
     * 底注
     */
    private int baseScore;
    /**
     * 玩家最大数量
     */
    private int maxPlayerNum;
    /**
     * 玩家列表
     */
    private Player[] players;
    /**
     * 状态，1等待中，2游戏中
     */
    private int status;

    public int validPlayerNum() {
        int num = 0;
        for (Player player : players) {
            if (player != null) {
                num++;
            }
        }
        return num;
    }
}
