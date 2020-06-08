package com.imooc.netty.core.$26.client.render;

import com.imooc.netty.core.$26.client.mock.MockClient;
import com.imooc.netty.core.$26.common.msg.CreateTableResponse;
import com.imooc.netty.core.$26.common.msg.EnterTableResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EnterTableResponseRender implements MahjongRender<EnterTableResponse> {

    @Override
    public void render(EnterTableResponse response) {
        MockClient.enterTableResponse(response);
    }
}
