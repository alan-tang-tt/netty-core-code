package com.imooc.netty.core.$26.common.protocol.mahjong;

import com.imooc.netty.core.$26.common.protocol.Response;
import com.imooc.netty.core.$26.common.protocol.domain.OperationEnum;

public class ResponseMahjongProtocol extends AbstractMahjongProtocol<Response> {
    @Override
    protected Class<? extends Response> bodyType(int opcode) {
        return OperationEnum.parseResponseType(opcode);
    }
}
