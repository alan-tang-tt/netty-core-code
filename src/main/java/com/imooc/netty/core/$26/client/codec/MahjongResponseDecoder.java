package com.imooc.netty.core.$26.client.codec;

import com.imooc.netty.core.$26.common.protocol.mahjong.ResponseMahjongProtocol;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * @author: tangtong
 * @date: 2020/6/5
 */
public class MahjongResponseDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        ResponseMahjongProtocol responseMahjongProtocol = new ResponseMahjongProtocol();
        responseMahjongProtocol.decode(msg);

        out.add(responseMahjongProtocol);
    }
}
