package com.imooc.netty.core.$998.client.render;

import com.imooc.netty.core.$998.client.mock.MockClient;
import com.imooc.netty.core.$998.common.msg.CreateTableResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateTableResponseRender implements MahjongRender<CreateTableResponse> {

    @Override
    public void render(CreateTableResponse response) {
        MockClient.createTableResponse(response);
    }
}
