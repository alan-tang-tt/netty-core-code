package com.imooc.netty.mahjong.client.render;

import com.imooc.netty.mahjong.common.protocol.MahjongMessage;

public interface MahjongRender<T extends MahjongMessage> {
    void render(T message);
}
