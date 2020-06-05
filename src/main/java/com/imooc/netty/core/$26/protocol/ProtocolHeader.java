package com.imooc.netty.core.$26.protocol;

import io.netty.buffer.ByteBuf;

public interface ProtocolHeader {
    void encode(ByteBuf buf);
    void decode(ByteBuf buf);
}
