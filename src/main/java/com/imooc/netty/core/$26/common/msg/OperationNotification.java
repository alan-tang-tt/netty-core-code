package com.imooc.netty.core.$26.common.msg;

import com.imooc.netty.core.$26.common.protocol.MahjongNotification;
import lombok.Data;

/**
 * 提示玩家操作通知
 */
@Data
public class OperationNotification implements MahjongNotification {
    /**
     * 什么操作
     */
    private int operation;
    /**
     * 什么位置
     */
    private int operationPos;
    /**
     * 等待多长时间
     */
    private int delayTime;
    /**
     * 通知发出时桌子的序列号
     */
    private int sequence;
    /**
     * 哪张牌触发的操作
     */
    private byte card;
}
