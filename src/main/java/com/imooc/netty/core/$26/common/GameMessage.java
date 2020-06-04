package com.imooc.netty.core.$26.common;

public interface GameMessage<B extends GameMessageBody> extends GameMessageCodec {
    GameMessageHeader header();
    B body();
    Class<B> bodyType();
}
