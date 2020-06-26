package com.imooc.netty.mahjong.server.processor;

import com.imooc.netty.mahjong.common.domain.Player;
import com.imooc.netty.mahjong.common.msg.LoginRequest;
import com.imooc.netty.mahjong.common.msg.LoginResponse;
import com.imooc.netty.mahjong.common.util.MahjongContext;
import com.imooc.netty.mahjong.common.util.MessageUtils;
import com.imooc.netty.mahjong.server.data.DataManager;
import com.imooc.netty.mahjong.server.util.IdUtils;

import java.util.concurrent.ThreadLocalRandom;

public class LoginRequestProcessor implements MahjongProcessor<LoginRequest> {
    @Override
    public void process(LoginRequest message) {
        LoginResponse response = new LoginResponse();
        if (message.getUsername() == null) {
            response.setResult(false);
            response.setMessage("username error");
            MessageUtils.sendResponse(response);
            return;
        }

        // 假设从数据库读取了Player信息加载到内存中
        Player player = new Player();
        player.setId(IdUtils.generateId());
        player.setUsername(message.getUsername());
        player.setPassword("");
        player.setScore(ThreadLocalRandom.current().nextInt(1000));
        DataManager.setChannelPlayer(MahjongContext.currentContext().getCurrentChannel(), player);

        response.setResult(true);
        response.setPlayer(player);
        response.setMessage("success");
        MessageUtils.sendResponse(response);
    }
}
