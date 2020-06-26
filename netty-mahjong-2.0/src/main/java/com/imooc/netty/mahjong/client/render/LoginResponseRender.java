package com.imooc.netty.mahjong.client.render;

import com.imooc.netty.mahjong.client.mock.MockClient;
import com.imooc.netty.mahjong.common.proto.LoginResponse;

public class LoginResponseRender implements MahjongRender<LoginResponse> {
    @Override
    public void render(LoginResponse message) {
        MockClient.loginResponse(message);
    }
}
