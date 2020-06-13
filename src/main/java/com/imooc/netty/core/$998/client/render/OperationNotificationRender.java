package com.imooc.netty.core.$998.client.render;

import com.imooc.netty.core.$998.client.mock.MockClient;
import com.imooc.netty.core.$998.common.msg.OperationNotification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OperationNotificationRender implements MahjongRender<OperationNotification> {

    @Override
    public void render(OperationNotification response) {
        MockClient.operationNotification(response);
    }
}
