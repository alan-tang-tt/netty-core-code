package com.imooc.netty.core.$26.protocol.mongo;

import com.imooc.netty.core.$26.protocol.ProtocolHeader;
import io.netty.buffer.ByteBuf;

public class MongoProtocolHeader implements ProtocolHeader {

    private int version;
    private int opcode;
    private long requestId;

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

    public int getVersion() {
        return version;
    }

    public int getOpcode() {
        return opcode;
    }

    public long getRequestId() {
        return requestId;
    }
}
