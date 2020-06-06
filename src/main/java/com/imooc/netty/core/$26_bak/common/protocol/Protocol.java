package com.imooc.netty.core.$26_bak.common.protocol;

import io.netty.buffer.ByteBuf;

public interface Protocol<H extends ProtocolHeader, B extends ProtocolBody> {
    void encode(ByteBuf buf);
    void decode(ByteBuf buf);
}
