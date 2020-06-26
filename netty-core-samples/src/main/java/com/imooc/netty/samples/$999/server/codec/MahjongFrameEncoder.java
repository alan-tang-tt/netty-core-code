package com.imooc.netty.samples.$999.server.codec;

import io.netty.handler.codec.LengthFieldPrepender;

/**
 * 一次编码器，将协议按一定格式进行编码
 */
public class MahjongFrameEncoder extends LengthFieldPrepender {
    public MahjongFrameEncoder() {
        super(2, 0);
    }
}
