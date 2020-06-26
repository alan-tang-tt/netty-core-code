package com.imooc.netty.mahjong.client.render;

import com.imooc.netty.mahjong.client.mock.MockClient;
import com.imooc.netty.mahjong.common.proto.GameOverNotification;

public class GameOverNotificationRender implements MahjongRender<GameOverNotification> {
    @Override
    public void render(GameOverNotification message) {
        MockClient.gameOverNotification(message);
    }
}
