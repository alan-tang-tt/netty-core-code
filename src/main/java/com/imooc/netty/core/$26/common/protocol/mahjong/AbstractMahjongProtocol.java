package com.imooc.netty.core.$26.common.protocol.mahjong;

import com.alibaba.fastjson.JSON;
import com.imooc.netty.core.$26.common.protocol.Protocol;
import com.imooc.netty.core.$26.common.protocol.ProtocolBody;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public abstract class AbstractMahjongProtocol<B extends ProtocolBody> implements Protocol<MahjongProtocolHeader, B> {
    private MahjongProtocolHeader header;
    private B body;

    @Override
    public void encode(ByteBuf buf) {
        header.encode(buf);
        buf.writeBytes(JSON.toJSONString(body).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void decode(ByteBuf buf) {
        header = new MahjongProtocolHeader();
        header.decode(buf);

        int opcode = header.getOpcode();
        Class<?> bodyType = bodyType(opcode);

        body = (B) JSON.parseObject(buf.toString(StandardCharsets.UTF_8), bodyType);
    }

    protected abstract Class<? extends B> bodyType(int opcode);

    public MahjongProtocolHeader getHeader() {
        return header;
    }

    public B getBody() {
        return body;
    }

    public void setHeader(MahjongProtocolHeader header) {
        this.header = header;
    }

    public void setBody(B body) {
        this.body = body;
    }
}
