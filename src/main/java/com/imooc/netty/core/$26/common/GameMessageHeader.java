package com.imooc.netty.core.$26.common;

public interface GameMessageHeader extends GameMessageCodec {

    int version();
    int opcode();
    long requestId();
}
