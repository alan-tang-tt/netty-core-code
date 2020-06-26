package com.imooc.netty.mahjong.client.render;

import com.imooc.netty.mahjong.client.mock.MockClient;
import com.imooc.netty.mahjong.common.msg.GameOverNotification;
import com.imooc.netty.mahjong.common.msg.SettlementNotification;

public class SettlementNotificationRender implements MahjongRender<SettlementNotification> {
    @Override
    public void render(SettlementNotification message) {
        MockClient.settlementNotification(message);
    }
}
