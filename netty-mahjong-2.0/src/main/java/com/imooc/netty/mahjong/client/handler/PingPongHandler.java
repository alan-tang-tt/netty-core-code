package com.imooc.netty.mahjong.client.handler;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.websocketx.PingWebSocketFrame;
import io.netty.handler.codec.http.websocketx.PongWebSocketFrame;
import io.netty.handler.timeout.IdleStateEvent;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class PingPongHandler extends SimpleChannelInboundHandler<PongWebSocketFrame> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, PongWebSocketFrame msg) throws Exception {
        // 返回pong响应
        log.info("client received pong response");
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        // 检测到是写空闲事件
        if (evt == IdleStateEvent.FIRST_WRITER_IDLE_STATE_EVENT) {
            log.info("idle check, send ping request");

            // 发送ping请求
            ByteBuf buffer = ctx.alloc().buffer();
            buffer.writeBytes(new byte[] {6, 6, 6});
            PingWebSocketFrame frame = new PingWebSocketFrame(buffer);
            ctx.writeAndFlush(frame);
        }
        super.userEventTriggered(ctx, evt);
    }
}
