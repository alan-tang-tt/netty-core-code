package com.imooc.netty.core.$26.client.render;

import com.imooc.netty.core.$26.client.mock.MockClient;
import com.imooc.netty.core.$26.common.msg.LoginResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginResponseRender implements MahjongRender<LoginResponse> {

    @Override
    public void render(LoginResponse response) {
        MockClient.loginResponse(response);
    }
}
