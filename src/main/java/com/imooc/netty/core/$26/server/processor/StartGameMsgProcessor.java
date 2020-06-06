package com.imooc.netty.core.$26.server.processor;

import com.imooc.netty.core.$26.common.domain.Table;
import com.imooc.netty.core.$26.common.msg.StartGameMsg;

public class StartGameMsgProcessor implements MahjongProcessor<StartGameMsg> {
    @Override
    public void process(StartGameMsg msg) {
        Table table = msg.getTable();

        // 洗牌

        // 发牌

        // 通知所有玩家

        // 通知庄家出牌




    }
}
