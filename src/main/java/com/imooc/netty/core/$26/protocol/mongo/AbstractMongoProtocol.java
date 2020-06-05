package com.imooc.netty.core.$26.protocol.mongo;

import com.alibaba.fastjson.JSON;
import com.imooc.netty.core.$26.protocol.Protocol;
import com.imooc.netty.core.$26.protocol.ProtocolBody;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public abstract class AbstractMongoProtocol<B extends ProtocolBody> implements Protocol<MongoProtocolHeader, B> {
    private MongoProtocolHeader header;
    private B body;

    @Override
    public void encode(ByteBuf buf) {
        header.encode(buf);
        buf.writeBytes(JSON.toJSONString(body).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void decode(ByteBuf buf) {
        header.decode(buf);

        int opcode = header.getOpcode();
        Class<B> bodyType = bodyType(opcode);

        body = JSON.parseObject(buf.toString(StandardCharsets.UTF_8), bodyType);
    }

    protected abstract Class<B> bodyType(int opcode);
}
