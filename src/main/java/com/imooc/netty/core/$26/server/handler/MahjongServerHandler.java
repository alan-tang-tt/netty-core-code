package com.imooc.netty.core.$26.server.handler;

import com.alibaba.fastjson.JSON;
import com.imooc.netty.core.$26.common.protocol.Request;
import com.imooc.netty.core.$26.common.protocol.Response;
import com.imooc.netty.core.$26.common.protocol.mahjong.RequestMahjongProtocol;
import com.imooc.netty.core.$26.common.protocol.mahjong.ResponseMahjongProtocol;
import io.netty.channel.ChannelHandler.Sharable;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * 业务逻辑处理器
 */
@Sharable
public class MahjongServerHandler extends SimpleChannelInboundHandler<RequestMahjongProtocol> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RequestMahjongProtocol msg) throws Exception {
        Request request = msg.getBody();
        Response response = request.operate();

        ResponseMahjongProtocol responseMahjongProtocol = new ResponseMahjongProtocol();
        responseMahjongProtocol.setHeader(msg.getHeader());
        responseMahjongProtocol.setBody(response);

        System.out.println("receive request: " + JSON.toJSONString(msg));

        System.out.println("send response: " + JSON.toJSONString(responseMahjongProtocol));

        ctx.writeAndFlush(responseMahjongProtocol);
    }
}
