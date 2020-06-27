package com.imooc.netty.mahjong.server;

import com.imooc.netty.mahjong.common.codec.MahjongProtocolDecoder;
import com.imooc.netty.mahjong.common.codec.MahjongProtocolEncoder;
import com.imooc.netty.mahjong.common.codec.BinaryWebSocketFrameDecoder;
import com.imooc.netty.mahjong.common.codec.BinaryWebSocketFrameEncoder;
import com.imooc.netty.mahjong.server.handler.MahjongServerHandler;
import com.imooc.netty.mahjong.server.util.MetricsUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

public class MahjongServer {

    private static final String WEBSOCKET_PATH = "/websocket";
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
                            // 打印日志
                            p.addLast(new LoggingHandler(LogLevel.INFO));

                            // 添加Http协议编解码器、处理器
                            p.addLast(new HttpServerCodec());
                            p.addLast(new HttpObjectAggregator(65536));
                            // 添加WebSocket处理器
                            p.addLast(new WebSocketServerCompressionHandler());
                            p.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));
                            // websocket编解码器
                            p.addLast(new BinaryWebSocketFrameDecoder());
                            p.addLast(new BinaryWebSocketFrameEncoder());

                            // 一次编解码器
//                            p.addLast(new ProtobufVarint32FrameDecoder());
//                            p.addLast(new ProtobufVarint32LengthFieldPrepender());
                            // 二次编解码器
                            p.addLast(new MahjongProtocolDecoder());
                            p.addLast(new MahjongProtocolEncoder());
                            // 处理器
                            p.addLast(new MahjongServerHandler());
                        }
                    });

            // 8. 绑定端口
            ChannelFuture f = serverBootstrap.bind(PORT).sync();

            // 添加监控
            MetricsUtils.start();

            // 9. 等待服务端监听端口关闭，这里会阻塞主线程
            f.channel().closeFuture().sync();
        } finally {
            // 10. 优雅地关闭两个线程池
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
