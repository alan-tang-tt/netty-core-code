package com.imooc.netty.core.$26.common;

import io.netty.buffer.ByteBuf;

public class DefaultGameMessageHeader implements GameMessageHeader {
    private int version;
    private int opcode;
    private long requestId;

    @Override
    public int version() {
        return version;
    }

    @Override
    public int opcode() {
        return opcode;
    }

    @Override
    public long requestId() {
        return requestId;
    }

    @Override
    public void encode(ByteBuf buf) {
        buf.writeInt(version);
        buf.writeInt(opcode);
        buf.writeLong(requestId);
    }

    @Override
    public void decode(ByteBuf buf) {
        this.version = buf.readInt();
        this.opcode = buf.readInt();
        this.requestId = buf.readLong();
    }
}
