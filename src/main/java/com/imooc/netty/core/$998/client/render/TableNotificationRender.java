package com.imooc.netty.core.$998.client.render;

import com.imooc.netty.core.$998.client.mock.MockClient;
import com.imooc.netty.core.$998.common.msg.TableNotification;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TableNotificationRender implements MahjongRender<TableNotification> {

    @Override
    public void render(TableNotification response) {
        MockClient.tableNotification(response);
    }
}
