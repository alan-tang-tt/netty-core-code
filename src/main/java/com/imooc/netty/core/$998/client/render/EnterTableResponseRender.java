package com.imooc.netty.core.$998.client.render;

import com.imooc.netty.core.$998.client.mock.MockClient;
import com.imooc.netty.core.$998.common.msg.EnterTableResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnterTableResponseRender implements MahjongRender<EnterTableResponse> {

    @Override
    public void render(EnterTableResponse response) {
        MockClient.enterTableResponse(response);
    }
}
