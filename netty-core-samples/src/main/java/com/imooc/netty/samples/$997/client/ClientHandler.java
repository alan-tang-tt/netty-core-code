package com.imooc.netty.samples.$997.client;

import com.imooc.netty.samples.$997.msg.BaseMsg;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class ClientHandler extends SimpleChannelInboundHandler<BaseMsg> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, BaseMsg msg) throws Exception {
        System.out.println(msg);
    }
}
