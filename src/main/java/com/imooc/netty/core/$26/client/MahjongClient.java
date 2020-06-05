package com.imooc.netty.core.$26.client;

import com.imooc.netty.core.$26.client.codec.MahjongFrameDecoder;
import com.imooc.netty.core.$26.client.codec.MahjongFrameEncoder;
import com.imooc.netty.core.$26.client.codec.MahjongRequestEncoder;
import com.imooc.netty.core.$26.client.codec.MahjongResponseDecoder;
import com.imooc.netty.core.$26.client.handler.MahjongClientHandler;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;

/**
 * @author: tangtong
 * @date: 2020/6/5
 */
public class MahjongClient {

    private static final int PORT = 8080;

    public static void main(String[] args) {

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

                    pipeline.addLast(new MahjongResponseDecoder());
                    pipeline.addLast(new MahjongRequestEncoder());

                    pipeline.addLast(new MahjongClientHandler());
                }
            });

            ChannelFuture future = bootstrap.connect(new InetSocketAddress(PORT)).sync();

            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {

        }
    }
}
