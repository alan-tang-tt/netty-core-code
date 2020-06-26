package com.imooc.netty.mahjong.common.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

public class MahjongFrameDecoder extends LengthFieldBasedFrameDecoder {
    public MahjongFrameDecoder() {
        super(65535, 0, 2, 0, 2);
    }
}
