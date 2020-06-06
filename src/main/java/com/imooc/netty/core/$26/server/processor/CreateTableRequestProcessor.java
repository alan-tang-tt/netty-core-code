package com.imooc.netty.core.$26.server.processor;

import com.imooc.netty.core.$26.common.domain.Player;
import com.imooc.netty.core.$26.common.domain.Table;
import com.imooc.netty.core.$26.common.msg.CreateTableRequest;
import com.imooc.netty.core.$26.common.msg.CreateTableResponse;
import com.imooc.netty.core.$26.server.data.DataManager;
import com.imooc.netty.core.$26.util.MsgUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CreateTableRequestProcessor implements MahjongProcessor<CreateTableRequest> {
    @Override
    public void process(CreateTableRequest msg) {
        // 创建桌子
        Table table = new Table();
        table.setId(DataManager.CURRENT_TABLE_ID.get());
        table.setBaseScore(msg.getBaseScore());
        table.setMaxPlayerNum(msg.getPlayerNum());
        Player[] players = new Player[table.getMaxPlayerNum()];
        players[0] = DataManager.currentPlayer();
        table.setPlayers(players);
        table.setStatus(1);

        // 数据缓存
        DataManager.bindChannelTable(DataManager.CURRENT_CHANNEL.get(), table);
        DataManager.putTable(table);

        // 返回响应
        CreateTableResponse response = new CreateTableResponse();
        response.setTable(table);
        MsgUtils.send(response);
    }
}
