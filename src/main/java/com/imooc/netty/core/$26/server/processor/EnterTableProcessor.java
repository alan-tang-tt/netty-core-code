package com.imooc.netty.core.$26.server.processor;

import com.imooc.netty.core.$26.common.domain.Player;
import com.imooc.netty.core.$26.common.domain.Table;
import com.imooc.netty.core.$26.common.msg.TableNotification;
import com.imooc.netty.core.$26.common.msg.EnterTableRequest;
import com.imooc.netty.core.$26.common.msg.EnterTableResponse;
import com.imooc.netty.core.$26.common.msg.StartGameMsg;
import com.imooc.netty.core.$26.server.data.DataManager;
import com.imooc.netty.core.$26.util.MsgUtils;
import io.netty.util.concurrent.EventExecutor;

public class EnterTableProcessor implements MahjongProcessor<EnterTableRequest> {

    @Override
    public void process(EnterTableRequest msg) {
        // 检查桌子是否存在
        Table table = DataManager.getTableById(msg.getTableId());
        if (table == null) {
            EnterTableResponse response = new EnterTableResponse();
            response.setResult(false);
            response.setMsg("桌子不存在");
            MsgUtils.send(response);
        }

        // 加入桌子
        Player[] players = table.getPlayers();
        for (int i = 0; i < players.length; i++) {
            if (players[i] == null) {
                players[i] = DataManager.currentPlayer();
                break;
            }
        }

        // 返回响应
        EnterTableResponse response = new EnterTableResponse();
        response.setResult(true);
        MsgUtils.send(response);

        // 通知所有玩家有新玩家加入
        TableNotification notification = new TableNotification();
        notification.setTable(table);
        MsgUtils.sendTableNotification(notification, false);

        // 如果达到最大人数，直接开始游戏
        if (table.validPlayerNum() == table.getMaxPlayerNum()) {
            EventExecutor executor = DataManager.CURRENT_EXECUTOR.get();
            if (executor.inEventLoop()) {
                startGame(table);
            } else {
                executor.execute(() -> startGame(table));
            }
        }
    }

    private void startGame(Table table) {
        StartGameMsg startGameMsg = new StartGameMsg();
        startGameMsg.setTable(table);
        MahjongProcessor.processMsg(startGameMsg);
    }
}
