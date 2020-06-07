package com.imooc.netty.core.$26.client;

import com.imooc.netty.core.$26.client.handler.MahjongClientHandler;
import com.imooc.netty.core.$26.common.codec.MahjongFrameDecoder;
import com.imooc.netty.core.$26.common.codec.MahjongFrameEncoder;
import com.imooc.netty.core.$26.common.codec.MahjongProtocolDecoder;
import com.imooc.netty.core.$26.common.codec.MahjongProtocolEncoder;
import com.imooc.netty.core.$26.common.msg.LoginRequest;
import com.imooc.netty.core.$26.util.MsgUtils;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.DefaultEventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;

import java.net.InetSocketAddress;
import java.util.Scanner;

public class MahjongClient {

    static final int PORT = Integer.parseInt(System.getProperty("port", "8080"));

    public static void main(String[] args) throws Exception {

        DefaultEventLoopGroup businessGroup = new DefaultEventLoopGroup(1);
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

                    pipeline.addLast(businessGroup, new MahjongClientHandler());
                }
            });

            ChannelFuture future = bootstrap.connect(new InetSocketAddress(PORT)).sync();

            businessGroup.execute(() -> {
                System.out.println("已连接到服务器，请选择您要进行的操作：1. 登录");
                Scanner scanner = new Scanner(System.in);
                boolean flag = false;
                while (scanner.hasNextLine()) {
                    String line = scanner.nextLine();
                    if ("1".equals(line)) {
                        flag = true;
                        System.out.println("请输入用户名和密码，以空格分隔：");
                    } else if (flag && line.contains(" ")) {
                        String[] strings = line.split(" ");
                        LoginRequest loginRequest = new LoginRequest();
                        loginRequest.setUsername(strings[0]);
                        loginRequest.setPassword(strings[1]);
                        MsgUtils.send(future.channel(), loginRequest);
                        break;
                    } else {
                        System.out.println("错误的输入，请重新输入：");
                    }
                }
                scanner.close();
            });

            future.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
        }
    }
}
