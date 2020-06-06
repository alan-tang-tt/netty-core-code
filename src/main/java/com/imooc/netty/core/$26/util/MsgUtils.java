package com.imooc.netty.core.$26.util;

import com.alibaba.fastjson.JSON;
import com.imooc.netty.core.$26.common.domain.Player;
import com.imooc.netty.core.$26.common.domain.Table;
import com.imooc.netty.core.$26.common.protocol.MahjongMsg;
import com.imooc.netty.core.$26.common.protocol.MahjongProtocol;
import com.imooc.netty.core.$26.server.data.DataManager;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MsgUtils {

    public static void send(MahjongMsg msg) {
        Channel channel = DataManager.CURRENT_CHANNEL.get();
        send(channel, msg);
    }

    private static void send(Channel channel, MahjongMsg msg) {
        if (channel != null && channel.isActive() && channel.isWritable()) {
            log.info("send channel msg: {}", JSON.toJSONString(msg));
            channel.writeAndFlush(new MahjongProtocol<>(msg));
        } else {
            log.error("channel unavailable, msgType={}", msg.getClass().getSimpleName());
        }
    }

    public static void send2Table(Table table, MahjongMsg msg) {
        for (Player player : table.getPlayers()) {
            if (player != null) {
                send2Player(player, msg);
            }
        }
    }

    private static void send2Player(Player player, MahjongMsg msg) {
        Channel channel = DataManager.getChannelByPlayerId(player.getId());
        send(channel, msg);
    }
}
