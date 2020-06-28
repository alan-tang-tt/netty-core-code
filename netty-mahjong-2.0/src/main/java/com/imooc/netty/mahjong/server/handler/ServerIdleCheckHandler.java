package com.imooc.netty.mahjong.server.handler;

import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.slf4j.Slf4j;

@ChannelHandler.Sharable
@Slf4j
public class ServerIdleCheckHandler extends IdleStateHandler {
    // 重写构造方法
    public ServerIdleCheckHandler() {
        // 10秒钟没收到读事件就认为是空闲了
        super(60, 0, 0);
    }

    @Override
    protected void channelIdle(ChannelHandlerContext ctx, IdleStateEvent evt) throws Exception {
        if (evt == IdleStateEvent.FIRST_READER_IDLE_STATE_EVENT) {
            log.error("idle check close the client");
            // 检测到读空闲则关闭连接
            ctx.close();
            return;
        }
        super.channelIdle(ctx, evt);
    }
}
