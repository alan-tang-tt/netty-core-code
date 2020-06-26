package com.imooc.netty.mahjong.client.render;

import com.imooc.netty.mahjong.client.mock.MockClient;
import com.imooc.netty.mahjong.common.msg.OperationNotification;
import com.imooc.netty.mahjong.common.msg.RoomRefreshNotification;

public class OperationNotificationRender implements MahjongRender<OperationNotification> {
    @Override
    public void render(OperationNotification message) {
        MockClient.operationNotification(message);
    }
}
