package com.imooc.netty.core.$26.client.render;

import com.imooc.netty.core.$26.common.msg.LoginResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LoginResponseRender implements MahjongRender<LoginResponse> {

    @Override
    public void render(LoginResponse response) {
        if (response.isResult()) {
            System.out.println("恭喜您，登录成功，请选择您的操作：1创建房间，2加入房间");
        } else {
            System.out.println("登录失败！");
        }
    }
}
