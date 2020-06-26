package com.imooc.netty.mahjong.client.render;

import com.google.protobuf.MessageLite;

public interface MahjongRender<T extends MessageLite> {
    void render(T message);
}
