package com.imooc.netty.core.$998.server.processor;

import com.imooc.netty.core.$998.common.domain.Player;
import com.imooc.netty.core.$998.common.domain.Table;
import com.imooc.netty.core.$998.common.msg.TableNotification;
import com.imooc.netty.core.$998.common.msg.EnterTableRequest;
import com.imooc.netty.core.$998.common.msg.EnterTableResponse;
import com.imooc.netty.core.$998.common.msg.StartGameMsg;
import com.imooc.netty.core.$998.server.data.DataManager;
import com.imooc.netty.core.$998.util.MsgUtils;

import java.util.Collections;

public class EnterTableRequestProcessor implements MahjongProcessor<EnterTableRequest> {

    @Override
    public void process(EnterTableRequest msg) {
        Player player = DataManager.currentPlayer();
        // 检查桌子是否存在
        Table table = DataManager.getTableById(msg.getTableId());
        if (table == null) {
            EnterTableResponse response = new EnterTableResponse();
            response.setResult(false);
            response.setMsg("房间不存在");
            MsgUtils.send(response);
            return;
        }

        // 检查桌子是不是满了
        if (table.validPlayerNum() == table.getMaxPlayerNum()) {
            EnterTableResponse response = new EnterTableResponse();
            response.setResult(false);
            response.setMsg("房间已满，请加入其他房间");
            MsgUtils.send(response);
            return;
        }

        // 加入桌子
        Player[] players = table.getPlayers();
        for (int i = 0; i < players.length; i++) {
            if (players[i] == null) {
                players[i] = player;
                player.setPos(i);
                player.setDiamond(player.getDiamond()-1);
                break;
            }
        }

        // 绑定桌子到channel上
        DataManager.bindChannelTable(DataManager.CURRENT_CHANNEL.get(), table);

        // 返回响应
        EnterTableResponse response = new EnterTableResponse();
        response.setResult(true);
        response.setTable(table);
        MsgUtils.send(response);

        // 通知所有玩家有新玩家加入
        TableNotification notification = new TableNotification();
        notification.setTable(table);
        MsgUtils.send2TableExcept(table, notification.clone(), Collections.singletonList(player.getId()));

        // 如果达到最大人数，直接开始游戏
        if (table.validPlayerNum() == table.getMaxPlayerNum()) {
            startGame(table);
        }
    }

    private void startGame(Table table) {
        StartGameMsg startGameMsg = new StartGameMsg();
        startGameMsg.setTable(table);
        MahjongProcessor.processMsg(startGameMsg);
    }
}
