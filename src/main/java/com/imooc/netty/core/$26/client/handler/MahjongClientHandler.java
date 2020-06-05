package com.imooc.netty.core.$26.client.handler;

import com.alibaba.fastjson.JSON;
import com.imooc.netty.core.$26.common.protocol.domain.LoginRequest;
import com.imooc.netty.core.$26.common.protocol.domain.OperationEnum;
import com.imooc.netty.core.$26.common.protocol.mahjong.MahjongProtocolHeader;
import com.imooc.netty.core.$26.common.protocol.mahjong.RequestMahjongProtocol;
import com.imooc.netty.core.$26.common.protocol.mahjong.ResponseMahjongProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * @author: tangtong
 * @date: 2020/6/5
 */
public class MahjongClientHandler extends SimpleChannelInboundHandler<ResponseMahjongProtocol> {

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        LoginRequest loginRequest = new LoginRequest("tt", "123456");
        System.out.println("request: " + JSON.toJSONString(loginRequest));

        RequestMahjongProtocol requestMahjongProtocol = new RequestMahjongProtocol();
        requestMahjongProtocol.setHeader(new MahjongProtocolHeader(OperationEnum.Login.getOpcode()));
        requestMahjongProtocol.setBody(loginRequest);

        ctx.writeAndFlush(requestMahjongProtocol);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, ResponseMahjongProtocol msg) throws Exception {
        System.out.println("response: " + JSON.toJSONString(msg));
    }
}
