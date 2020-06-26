package com.imooc.netty.mahjong.client.render;

import com.imooc.netty.mahjong.client.mock.MockClient;
import com.imooc.netty.mahjong.common.msg.CreateRoomResponse;
import com.imooc.netty.mahjong.common.msg.EnterRoomResponse;

public class EnterRoomResponseRender implements MahjongRender<EnterRoomResponse> {
    @Override
    public void render(EnterRoomResponse message) {
        MockClient.enterRoomResponse(message);
    }
}
