package com.imooc.netty.core.$26.client.render;

import com.imooc.netty.core.$26.client.mock.MockClient;
import com.imooc.netty.core.$26.common.msg.OperationResultNotification;

public class OperationResultNotificationRender implements MahjongRender<OperationResultNotification> {
    @Override
    public void render(OperationResultNotification response) {
        MockClient.operationResultNotification(response);
    }
}
