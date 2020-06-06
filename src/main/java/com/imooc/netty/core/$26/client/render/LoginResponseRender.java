package com.imooc.netty.core.$26.client.render;

import com.alibaba.fastjson.JSON;
import com.imooc.netty.core.$26.common.msg.LoginResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginResponseRender implements MahjongRender<LoginResponse> {

    @Override
    public void render(LoginResponse response) {
        log.info("render login response, response={}", JSON.toJSONString(response));
    }
}
