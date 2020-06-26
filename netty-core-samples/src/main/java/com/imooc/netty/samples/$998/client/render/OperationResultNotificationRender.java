package com.imooc.netty.samples.$998.client.render;

import com.imooc.netty.samples.$998.client.mock.MockClient;
import com.imooc.netty.samples.$998.common.msg.OperationResultNotification;

public class OperationResultNotificationRender implements MahjongRender<OperationResultNotification> {
    @Override
    public void render(OperationResultNotification response) {
        MockClient.operationResultNotification(response);
    }
}
