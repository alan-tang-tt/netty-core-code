package com.imooc.netty.core.$997.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class FrameDecoder extends LengthFieldBasedFrameDecoder {
    public FrameDecoder() {
        super(65535, 0, 2, 0, 2);
    }
}
