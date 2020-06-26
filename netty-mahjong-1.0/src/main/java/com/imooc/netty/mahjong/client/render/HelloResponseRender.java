package com.imooc.netty.mahjong.client.render;

import com.imooc.netty.mahjong.client.mock.MockClient;
import com.imooc.netty.mahjong.common.msg.HelloResponse;

public class HelloResponseRender implements MahjongRender<HelloResponse> {
    @Override
    public void render(HelloResponse message) {
        MockClient.helloResponse(message);
    }
}
