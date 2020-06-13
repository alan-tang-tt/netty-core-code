package com.imooc.netty.core.$998.common.codec;

import io.netty.handler.codec.LengthFieldPrepender;

public class MahjongFrameEncoder extends LengthFieldPrepender {
    public MahjongFrameEncoder() {
        super(2);
    }
}
