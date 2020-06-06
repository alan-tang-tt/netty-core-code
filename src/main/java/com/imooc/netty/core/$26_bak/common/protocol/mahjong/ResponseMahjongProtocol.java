package com.imooc.netty.core.$26_bak.common.protocol.mahjong;

import com.imooc.netty.core.$26_bak.common.protocol.Response;
import com.imooc.netty.core.$26_bak.common.protocol.domain.OperationEnum;

public class ResponseMahjongProtocol extends AbstractMahjongProtocol<Response> {
    @Override
    protected Class<? extends Response> bodyType(int opcode) {
        return OperationEnum.parseResponseType(opcode);
    }
}
