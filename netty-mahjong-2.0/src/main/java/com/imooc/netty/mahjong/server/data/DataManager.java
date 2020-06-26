package com.imooc.netty.mahjong.server.data;

import com.imooc.netty.mahjong.common.domain.Player;
import io.netty.channel.Channel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DataManager {

    private static final Map<String, Player> CHANNEL_ID_TO_PLAYER = new ConcurrentHashMap<>();

    public static void setChannelPlayer(Channel channel, Player player) {
        CHANNEL_ID_TO_PLAYER.put(channel.id().asLongText(), player);
    }

    public static Player getChannelPlayer(Channel channel) {
        return CHANNEL_ID_TO_PLAYER.get(channel.id().asLongText());
    }
}
