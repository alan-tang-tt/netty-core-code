package com.imooc.netty.core.$997.codec;

import com.imooc.netty.core.$997.msg.BaseMsg;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class ProtoDecoder extends MessageToMessageDecoder<ByteBuf> {
    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        BaseMsg baseMsg = new BaseMsg();
        baseMsg.decode(msg);

        out.add(baseMsg);
    }
}
