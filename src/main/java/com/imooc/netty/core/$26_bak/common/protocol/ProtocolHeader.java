package com.imooc.netty.core.$26_bak.common.protocol;

import io.netty.buffer.ByteBuf;

public interface ProtocolHeader {
    void encode(ByteBuf buf);
    void decode(ByteBuf buf);
}
