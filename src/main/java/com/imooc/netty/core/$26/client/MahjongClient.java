package com.imooc.netty.core.$26.client;

import com.imooc.netty.core.$26.client.handler.MahjongClientHandler;
import com.imooc.netty.core.$26.common.msg.CreateTableRequest;
import com.imooc.netty.core.$26.common.msg.LoginRequest;
import com.imooc.netty.core.$26.common.codec.MahjongFrameDecoder;
import com.imooc.netty.core.$26.common.codec.MahjongFrameEncoder;
import com.imooc.netty.core.$26.common.codec.MahjongProtocolDecoder;
import com.imooc.netty.core.$26.common.codec.MahjongProtocolEncoder;
import com.imooc.netty.core.$26.util.MsgUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

public class MahjongClient {

    static final int PORT = Integer.parseInt(System.getProperty("port", "8080"));

    public static void main(String[] args)  throws Exception {

        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline pipeline = ch.pipeline();

                    pipeline.addLast(new LoggingHandler(LogLevel.INFO));

                    pipeline.addLast(new MahjongFrameDecoder());
                    pipeline.addLast(new MahjongFrameEncoder());

                    pipeline.addLast(new MahjongProtocolDecoder());
                    pipeline.addLast(new MahjongProtocolEncoder());

                    pipeline.addLast(new MahjongClientHandler());
                }
            });

            ChannelFuture future = bootstrap.connect(new InetSocketAddress(PORT)).sync();

            // 登录
            LoginRequest loginRequest = new LoginRequest();
            loginRequest.setUsername("tt01");
            loginRequest.setPassword("123456");
            MsgUtils.send(loginRequest);

            // 创建桌子
            CreateTableRequest createTableRequest = new CreateTableRequest();
            MsgUtils.send(createTableRequest);

            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
