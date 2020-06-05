package com.imooc.netty.core.$26.protocol.mongo;

import com.imooc.netty.core.$26.protocol.Response;

public class ResponseMongoProtocol extends AbstractMongoProtocol<Response> {
    @Override
    protected Class<Response> bodyType(int opcode) {
        return null;
    }
}
