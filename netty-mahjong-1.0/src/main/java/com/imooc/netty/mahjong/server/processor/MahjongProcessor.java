package com.imooc.netty.mahjong.server.processor;

import com.imooc.netty.mahjong.common.protocol.MahjongMessage;

public interface MahjongProcessor<T extends MahjongMessage> {
    void process(T message);
}
