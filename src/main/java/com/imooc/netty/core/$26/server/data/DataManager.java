package com.imooc.netty.core.$26.server.data;

import com.imooc.netty.core.$26.common.domain.Player;
import com.imooc.netty.core.$26.common.domain.Table;
import io.netty.channel.Channel;
import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.FastThreadLocal;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 数据管理
 */
public class DataManager {

    public static final FastThreadLocal<Channel> CURRENT_CHANNEL = new FastThreadLocal<>();
    public static final FastThreadLocal<Long> CURRENT_TABLE_ID = new FastThreadLocal<>();
    public static final FastThreadLocal<EventExecutor> CURRENT_EXECUTOR = new FastThreadLocal<>();

    private static final Map<Channel, ChannelBinding> CHANNEL_BINDING_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, Table> TABLE_MAP = new ConcurrentHashMap<>();
    private static final Map<Long, Channel> PLAYER_ID_2_CHANNEL_MAP = new ConcurrentHashMap<>();

    public static void bindPlayerChannel(Player player, Channel channel) {
        PLAYER_ID_2_CHANNEL_MAP.put(player.getId(), channel);
    }

    public static Channel getChannelByPlayerId(Long playerId) {
        return PLAYER_ID_2_CHANNEL_MAP.get(playerId);
    }

    public static Long getTableIdByChannel(Channel channel) {
        synchronized (channel) {
            ChannelBinding channelBinding = CHANNEL_BINDING_MAP.get(channel);
            if (channelBinding == null) {
                return null;
            }
            Table table = channelBinding.getTable();
            if (table == null) {
                return null;
            }
            return table.getId();
        }
    }

    public static void bindChannelPlayer(Channel channel, Player player) {
        synchronized (channel) {
            ChannelBinding channelBinding = CHANNEL_BINDING_MAP.get(channel);
            if (channelBinding == null) {
                channelBinding = new ChannelBinding();
                CHANNEL_BINDING_MAP.put(channel, channelBinding);
            }
            channelBinding.setPlayer(player);
        }
    }

    public static void bindChannelTable(Channel channel, Table table) {
        synchronized (channel) {
            ChannelBinding channelBinding = CHANNEL_BINDING_MAP.get(channel);
            if (channelBinding == null) {
                channelBinding = new ChannelBinding();
                CHANNEL_BINDING_MAP.put(channel, channelBinding);
            }
            channelBinding.setTable(table);
        }
    }

    public static Player currentPlayer() {
        Channel channel = CURRENT_CHANNEL.get();
        ChannelBinding channelBinding = CHANNEL_BINDING_MAP.get(channel);
        return channelBinding.getPlayer();
    }

    public static Table getTableById(Long tableId) {
        return TABLE_MAP.get(tableId);
    }

    public static void putTable(Table table) {
        TABLE_MAP.put(table.getId(), table);
    }
}
