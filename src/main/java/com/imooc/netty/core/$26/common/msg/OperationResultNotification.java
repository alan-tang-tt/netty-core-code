package com.imooc.netty.core.$26.common.msg;

import com.imooc.netty.core.$26.common.protocol.MahjongNotification;
import lombok.Data;

/**
 * 操作结果通知
 */
@Data
public class OperationResultNotification implements MahjongNotification {
    private int operation;
    private int operationPos;
    private byte[] cards;
}
