package com.imooc.netty.core.$26.client.render;

import com.imooc.netty.core.$26.client.mock.MockClient;
import com.imooc.netty.core.$26.common.msg.SettleNotification;
import com.imooc.netty.core.$26.common.msg.TableNotification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SettleNotificationRender implements MahjongRender<SettleNotification> {

    @Override
    public void render(SettleNotification response) {
        MockClient.settleNotification(response);
    }
}
