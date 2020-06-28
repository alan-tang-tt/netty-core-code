package com.imooc.netty.mahjong.server;

import com.imooc.netty.mahjong.common.codec.MahjongProtocolDecoder;
import com.imooc.netty.mahjong.common.codec.MahjongProtocolEncoder;
import com.imooc.netty.mahjong.common.codec.BinaryWebSocketFrameDecoder;
import com.imooc.netty.mahjong.common.codec.BinaryWebSocketFrameEncoder;
import com.imooc.netty.mahjong.server.handler.MahjongServerHandler;
import com.imooc.netty.mahjong.server.handler.ServerIdleCheckHandler;
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
import io.netty.handler.ipfilter.IpFilterRule;
import io.netty.handler.ipfilter.IpFilterRuleType;
import io.netty.handler.ipfilter.IpSubnetFilterRule;
import io.netty.handler.ipfilter.RuleBasedIpFilter;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.SslHandler;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public class MahjongServer {

    private static final String WEBSOCKET_PATH = "/websocket";
    static final int PORT = Integer.parseInt(System.getProperty("port", "8443"));

    public static void main(String[] args) throws Exception {
        // ssl
        SelfSignedCertificate ssc = new SelfSignedCertificate();
        SslContext sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey())
                .build();
        System.out.println(ssc.certificate().getPath());

        // 1. 声明线程池
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {

            final LoggingHandler loggingHandler = new LoggingHandler(LogLevel.INFO);

            IpFilterRule ipFilterRule = new IpSubnetFilterRule("192.168.175.1", 8, IpFilterRuleType.ACCEPT);
            RuleBasedIpFilter ruleBasedIpFilter = new RuleBasedIpFilter(ipFilterRule);

            ServerIdleCheckHandler serverIdleCheckHandler = new ServerIdleCheckHandler();

            BinaryWebSocketFrameDecoder binaryWebSocketFrameDecoder = new BinaryWebSocketFrameDecoder();
            BinaryWebSocketFrameEncoder binaryWebSocketFrameEncoder = new BinaryWebSocketFrameEncoder();

            MahjongProtocolDecoder mahjongProtocolDecoder = new MahjongProtocolDecoder();
            MahjongProtocolEncoder mahjongProtocolEncoder = new MahjongProtocolEncoder();

            MahjongServerHandler mahjongServerHandler = new MahjongServerHandler();

            // 2. 服务端引导器
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            // 3. 设置线程池
            serverBootstrap.group(bossGroup, workerGroup)
                    // 4. 设置ServerSocketChannel的类型
                    .channel(NioServerSocketChannel.class)
                    // 5. 设置参数
                    .option(ChannelOption.SO_BACKLOG, 100)
                    // 6. 设置ServerSocketChannel对应的Handler，只能设置一个
                    .handler(loggingHandler)
                    // 7. 设置SocketChannel对应的Handler
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            // 打印日志
                            p.addLast(loggingHandler);

                            // 黑白名单过滤器
                            p.addLast(ruleBasedIpFilter);

                            // 空闲检测
                            p.addLast(serverIdleCheckHandler);

                            // ssl
//                            p.addLast(sslCtx.newHandler(ch.alloc()));

                            // 添加Http协议编解码器、处理器
                            p.addLast(new HttpServerCodec());
                            p.addLast(new HttpObjectAggregator(65536));
                            // 添加WebSocket处理器
                            p.addLast(new WebSocketServerCompressionHandler());
                            p.addLast(new WebSocketServerProtocolHandler(WEBSOCKET_PATH, null, true));

                            // websocket编解码器
                            p.addLast(binaryWebSocketFrameDecoder);
                            p.addLast(binaryWebSocketFrameEncoder);

                            // 一次编解码器
//                            p.addLast(new ProtobufVarint32FrameDecoder());
//                            p.addLast(new ProtobufVarint32LengthFieldPrepender());
                            // 二次编解码器
                            p.addLast(mahjongProtocolDecoder);
                            p.addLast(mahjongProtocolEncoder);
                            // 处理器
                            p.addLast(mahjongServerHandler);
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
