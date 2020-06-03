package com.imooc.netty.core.$26.server.handler;

import com.imooc.netty.core.$26.server.protocal.GameRequest;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 业务逻辑处理器
 */
@Sharable
public class GameServerHandler extends SimpleChannelInboundHandler<GameRequest> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, GameRequest msg) throws Exception {

    }
}
