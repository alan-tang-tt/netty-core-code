package com.imooc.netty.core.$998.common.msg;

import com.imooc.netty.core.$998.common.protocol.MahjongRequest;
import lombok.Data;

@Data
public class OperationRequest implements MahjongRequest {
    private int sequence;
    private int operation;
    private int operationPos;
    private byte[] cards;
}
