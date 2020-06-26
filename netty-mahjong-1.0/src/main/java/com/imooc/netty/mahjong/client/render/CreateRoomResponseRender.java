package com.imooc.netty.mahjong.client.render;

import com.imooc.netty.mahjong.client.mock.MockClient;
import com.imooc.netty.mahjong.common.msg.CreateRoomResponse;

public class CreateRoomResponseRender implements MahjongRender<CreateRoomResponse> {
    @Override
    public void render(CreateRoomResponse message) {
        MockClient.createRoomResponse(message);
    }
}
