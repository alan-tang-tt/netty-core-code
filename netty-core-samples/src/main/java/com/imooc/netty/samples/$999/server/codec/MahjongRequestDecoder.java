package com.imooc.netty.samples.$999.server.codec;

import com.imooc.netty.samples.$999.common.protocol.mahjong.RequestMahjongProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * 将经过一次解码之后的ByteBuf，解码成对应的协议，相当于反序列化
 */
public class MahjongRequestDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        RequestMahjongProtocol requestMahjongProtocol = new RequestMahjongProtocol();
        requestMahjongProtocol.decode(msg);
        out.add(requestMahjongProtocol);
    }
}
