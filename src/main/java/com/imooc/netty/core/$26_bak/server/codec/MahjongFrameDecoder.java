package com.imooc.netty.core.$26_bak.server.codec;

import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * 一次解码器，按协议格式解码
 */
public class MahjongFrameDecoder extends LengthFieldBasedFrameDecoder {
    public MahjongFrameDecoder() {
        super(65535, 0, 2, 0, 2);
    }
}
