package com.imooc.netty.samples.$998.client.render;

import com.imooc.netty.samples.$998.common.msg.OperationNotification;
import com.imooc.netty.samples.$998.client.mock.MockClient;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OperationNotificationRender implements MahjongRender<OperationNotification> {

    @Override
    public void render(OperationNotification response) {
        MockClient.operationNotification(response);
    }
}
