package com.imooc.netty.core.$997.codec;

import io.netty.handler.codec.LengthFieldPrepender;

public class FrameEncoder extends LengthFieldPrepender {

    public FrameEncoder() {
        super(2);
    }
}
