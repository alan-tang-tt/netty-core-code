package com.imooc.netty.core.$26.client.handler;

import com.imooc.netty.core.$26.client.render.MahjongRender;
import com.imooc.netty.core.$26.client.render.RenderEnum;
import com.imooc.netty.core.$26.common.protocol.MahjongMsg;
import com.imooc.netty.core.$26.common.protocol.MahjongProtocol;
import com.imooc.netty.core.$26.common.protocol.MahjongProtocolHeader;
import com.imooc.netty.core.$26.common.protocol.MahjongResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MahjongClientHandler extends SimpleChannelInboundHandler<MahjongProtocol> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MahjongProtocol mahjongProtocol) throws Exception {
        // process header
        MahjongProtocolHeader header = mahjongProtocol.getHeader();

        // we do nothing with header

        MahjongMsg body = mahjongProtocol.getBody();
        if (body instanceof MahjongResponse) {
            // render response
            MahjongResponse response = (MahjongResponse) body;
            MahjongRender render = RenderEnum.getRender(response.getClass());
            if (render != null) {
                render.render(response);
            } else {
                log.error("not found response render, msgType={}", body.getClass().getSimpleName());
            }
        } else {
            log.error("error msgType, just discard, msgType={}", body.getClass().getSimpleName());
        }
    }
}
