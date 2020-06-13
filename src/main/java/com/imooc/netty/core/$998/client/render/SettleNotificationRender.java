package com.imooc.netty.core.$998.client.render;

import com.imooc.netty.core.$998.client.mock.MockClient;
import com.imooc.netty.core.$998.common.msg.SettleNotification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SettleNotificationRender implements MahjongRender<SettleNotification> {

    @Override
    public void render(SettleNotification response) {
        MockClient.settleNotification(response);
    }
}
