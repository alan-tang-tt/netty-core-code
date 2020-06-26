package com.imooc.netty.samples.$34.client;

import com.imooc.netty.samples.$34.proto.HelloResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ProtobufClientHandler extends SimpleChannelInboundHandler<HelloResponse> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, HelloResponse msg) throws Exception {
        if (msg.getResult()) {
            System.out.println(msg.getMessage());
        }
    }
}
