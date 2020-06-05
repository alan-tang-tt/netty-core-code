package com.imooc.netty.core.$26.common.protocol.mahjong;

import com.imooc.netty.core.$26.common.protocol.Request;
import com.imooc.netty.core.$26.common.protocol.domain.OperationEnum;

public class RequestMahjongProtocol extends AbstractMahjongProtocol<Request> {

    @Override
    protected Class<? extends Request> bodyType(int opcode) {
        return OperationEnum.parseRequestType(opcode);
    }
}
