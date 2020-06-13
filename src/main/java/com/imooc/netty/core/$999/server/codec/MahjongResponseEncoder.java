package com.imooc.netty.core.$999.server.codec;

import com.imooc.netty.core.$999.common.protocol.mahjong.ResponseMahjongProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * 将具体的协议编码成ByteBuf，相当于序列化
 */
public class MahjongResponseEncoder extends MessageToByteEncoder<ResponseMahjongProtocol> {
    @Override
    protected void encode(ChannelHandlerContext ctx, ResponseMahjongProtocol msg, ByteBuf out) throws Exception {
        msg.encode(out);
    }
}
