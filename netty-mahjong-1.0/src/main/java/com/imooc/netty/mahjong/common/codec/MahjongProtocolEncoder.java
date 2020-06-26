package com.imooc.netty.mahjong.common.codec;

import com.imooc.netty.mahjong.common.protocol.MahjongProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class MahjongProtocolEncoder extends MessageToMessageEncoder<MahjongProtocol> {

    @Override
    protected void encode(ChannelHandlerContext ctx, MahjongProtocol mahjongProtocol, List<Object> out) throws Exception {
        ByteBuf buffer = ctx.alloc().buffer();
        mahjongProtocol.encode(buffer);
        out.add(buffer);
    }
}
