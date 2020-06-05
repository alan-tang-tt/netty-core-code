package com.imooc.netty.core.$26.protocol.mongo;

import com.imooc.netty.core.$26.protocol.Request;

public class RequestMongoProtocol extends AbstractMongoProtocol<Request> {

    @Override
    protected Class<Request> bodyType(int opcode) {
        return null;
    }
}
