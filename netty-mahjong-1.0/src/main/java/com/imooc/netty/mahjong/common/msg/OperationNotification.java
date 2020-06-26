package com.imooc.netty.mahjong.common.msg;

import com.imooc.netty.mahjong.common.protocol.MahjongMessage;
import lombok.Data;

@Data
public class OperationNotification implements MahjongMessage {
    private int operation;
    private int pos;
    private byte fireCard;
}
