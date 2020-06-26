package com.imooc.netty.mahjong.server.processor;

import com.imooc.netty.mahjong.common.domain.Player;
import com.imooc.netty.mahjong.common.proto.LoginRequest;
import com.imooc.netty.mahjong.common.proto.LoginResponse;
import com.imooc.netty.mahjong.common.util.MahjongContext;
import com.imooc.netty.mahjong.common.util.MessageUtils;
import com.imooc.netty.mahjong.server.data.DataManager;
import com.imooc.netty.mahjong.server.util.IdUtils;

import java.util.concurrent.ThreadLocalRandom;

public class LoginRequestProcessor implements MahjongProcessor<LoginRequest> {
    @Override
    public void process(LoginRequest message) {
        if (message.getUsername() == null) {
            LoginResponse response = LoginResponse
                    .newBuilder()
                    .setResult(false)
                    .setMessage("username error")
                    .build();
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

        LoginResponse response = LoginResponse
                .newBuilder()
                .setResult(true)
                .setPlayer(player.toPlayerMsg())
                .setMessage("success")
                .build();
        MessageUtils.sendResponse(response);
    }
}
