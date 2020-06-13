package com.imooc.netty.core.$999.client.codec;

import com.imooc.netty.core.$999.common.protocol.mahjong.RequestMahjongProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author: tangtong
 * @date: 2020/6/5
 */
public class MahjongRequestEncoder extends MessageToByteEncoder<RequestMahjongProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, RequestMahjongProtocol msg, ByteBuf out) throws Exception {
        msg.encode(out);
    }
}
