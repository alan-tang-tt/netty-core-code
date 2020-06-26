package com.imooc.netty.samples.$998.common.codec;

import io.netty.handler.codec.LengthFieldPrepender;

public class MahjongFrameEncoder extends LengthFieldPrepender {
    public MahjongFrameEncoder() {
        super(2);
    }
}
