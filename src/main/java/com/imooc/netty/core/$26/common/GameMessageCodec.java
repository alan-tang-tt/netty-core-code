package com.imooc.netty.core.$26.common;

import io.netty.buffer.ByteBuf;

public interface GameMessageCodec {
    void encode(ByteBuf buf);
    void decode(ByteBuf buf);
}
