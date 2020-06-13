package com.imooc.netty.core.$999.common.protocol.mahjong;

import com.imooc.netty.core.$999.common.protocol.Request;
import com.imooc.netty.core.$999.common.protocol.domain.OperationEnum;

public class RequestMahjongProtocol extends AbstractMahjongProtocol<Request> {

    @Override
    protected Class<? extends Request> bodyType(int opcode) {
        return OperationEnum.parseRequestType(opcode);
    }
}
