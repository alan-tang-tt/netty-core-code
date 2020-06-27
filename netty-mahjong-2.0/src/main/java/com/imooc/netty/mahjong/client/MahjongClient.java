package com.imooc.netty.mahjong.client;

import com.imooc.netty.mahjong.client.handler.MahjongClientHandler;
import com.imooc.netty.mahjong.client.handler.HandshakerClientHandler;
import com.imooc.netty.mahjong.client.mock.MockClient;
import com.imooc.netty.mahjong.common.codec.MahjongProtocolDecoder;
import com.imooc.netty.mahjong.common.codec.MahjongProtocolEncoder;
import com.imooc.netty.mahjong.common.codec.BinaryWebSocketFrameDecoder;
import com.imooc.netty.mahjong.common.codec.BinaryWebSocketFrameEncoder;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.DefaultHttpHeaders;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.websocketx.WebSocketClientHandshakerFactory;
import io.netty.handler.codec.http.websocketx.WebSocketVersion;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketClientCompressionHandler;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.net.URI;

@Slf4j
public class MahjongClient {

    static final String URL = System.getProperty("url", "ws://127.0.0.1:8080/websocket");

    public static void main(String[] args) throws Exception {
        // 工作线程池
        NioEventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            URI uri = new URI(URL);
            final HandshakerClientHandler handler =
                    new HandshakerClientHandler(
                            WebSocketClientHandshakerFactory.newHandshaker(
                                    uri, WebSocketVersion.V13, null, true, new DefaultHttpHeaders()));

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(workerGroup);
            bootstrap.channel(NioSocketChannel.class);
            bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline p = ch.pipeline();

                    // 打印日志
                    p.addLast(new LoggingHandler(LogLevel.INFO));

                    p.addLast(new HttpClientCodec());
                    p.addLast(new HttpObjectAggregator(8192));
                    p.addLast(WebSocketClientCompressionHandler.INSTANCE);
                    p.addLast(handler);

                    // websocket编解码器
                    p.addLast(new BinaryWebSocketFrameDecoder());
                    p.addLast(new BinaryWebSocketFrameEncoder());

                    // 一次编解码器
//                    p.addLast(new ProtobufVarint32FrameDecoder());
//                    p.addLast(new ProtobufVarint32LengthFieldPrepender());
                    // 二次编解码器
                    p.addLast(new MahjongProtocolDecoder());
                    p.addLast(new MahjongProtocolEncoder());
                    // 处理器
                    p.addLast(new MahjongClientHandler());
                }
            });

            // 连接到服务端
            ChannelFuture future = bootstrap.connect(new InetSocketAddress(uri.getPort())).sync();
            // 等待WebSocket握手完成
            handler.handshakeFuture().sync();

            log.info("connect to server success");

            MockClient.start(future.channel());

            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
