package com.imooc.netty.core.$26.common;

import com.alibaba.fastjson.JSON;
import io.netty.buffer.ByteBuf;

import java.nio.charset.StandardCharsets;

public class AbstractGameRequest implements GameRequest {

    @Override
    public void encode(ByteBuf buf) {
        buf.writeBytes(JSON.toJSONString(this).getBytes(StandardCharsets.UTF_8));
    }

    @Override
    public void decode(ByteBuf buf) {

    }

    @Override
    public void operate() {

    }
}
