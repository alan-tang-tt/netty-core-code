package com.imooc.netty.mahjong.client.render;

import com.imooc.netty.mahjong.client.mock.MockClient;
import com.imooc.netty.mahjong.common.proto.RoomRefreshNotification;

public class RoomRefreshNotificationRender implements MahjongRender<RoomRefreshNotification> {
    @Override
    public void render(RoomRefreshNotification message) {
        MockClient.roomRefreshNotification(message);
    }
}
