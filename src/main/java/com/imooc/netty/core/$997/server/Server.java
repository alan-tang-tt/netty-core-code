package com.imooc.netty.core.$997.server;

import com.imooc.netty.core.$997.codec.FrameDecoder;
import com.imooc.netty.core.$997.codec.FrameEncoder;
import com.imooc.netty.core.$997.codec.ProtoDecoder;
import com.imooc.netty.core.$997.codec.ProtoEncoder;
import com.imooc.netty.core.$998.common.codec.MahjongFrameDecoder;
import com.imooc.netty.core.$998.common.codec.MahjongFrameEncoder;
import com.imooc.netty.core.$998.common.codec.MahjongProtocolDecoder;
import com.imooc.netty.core.$998.common.codec.MahjongProtocolEncoder;
import com.imooc.netty.core.$998.server.handler.MahjongServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class Server {

    static final int PORT = Integer.parseInt(System.getProperty("port", "8080"));

    public static void main(String[] args) throws Exception {
        // 1. 声明线程池
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            // 2. 服务端引导器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 3. 设置线程池
            serverBootstrap.group(bossGroup, workerGroup)
                    // 4. 设置ServerSocketChannel的类型
                    .channel(NioServerSocketChannel.class)
                    // 5. 设置参数
                    .option(ChannelOption.SO_BACKLOG, 100)
                    // 6. 设置ServerSocketChannel对应的Handler，只能设置一个
                    .handler(new LoggingHandler(LogLevel.INFO))
                    // 7. 设置SocketChannel对应的Handler
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            // 可以添加多个子Handler
                            p.addLast(new LoggingHandler(LogLevel.INFO));

                            p.addLast(new FrameDecoder());
                            p.addLast(new FrameEncoder());

                            p.addLast(new ProtoDecoder());
                            p.addLast(new ProtoEncoder());

                            p.addLast(new DefaultEventLoopGroup(), new ServerHandler());
                        }
                    });

            // 8. 绑定端口
            ChannelFuture f = serverBootstrap.bind(PORT).sync();
            // 9. 等待服务端监听端口关闭，这里会阻塞主线程
            f.channel().closeFuture().sync();
        } finally {
            // 10. 优雅地关闭两个线程池
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
