package com.imooc.netty.mahjong.client.render;

import com.imooc.netty.mahjong.client.mock.MockClient;
import com.imooc.netty.mahjong.common.msg.OperationNotification;
import com.imooc.netty.mahjong.common.msg.OperationResultNotification;

public class OperationResultNotificationRender implements MahjongRender<OperationResultNotification> {
    @Override
    public void render(OperationResultNotification message) {
        MockClient.operationResultNotification(message);
    }
}
