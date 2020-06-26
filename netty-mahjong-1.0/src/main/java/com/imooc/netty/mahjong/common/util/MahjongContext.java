package com.imooc.netty.mahjong.common.util;

import com.imooc.netty.mahjong.common.domain.Player;
import com.imooc.netty.mahjong.common.domain.Room;
import com.imooc.netty.mahjong.common.msg.OperationNotification;
import com.imooc.netty.mahjong.common.msg.OperationRequest;
import com.imooc.netty.mahjong.common.protocol.MahjongProtocolHeader;
import io.netty.channel.Channel;
import io.netty.util.concurrent.FastThreadLocal;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 麻将上下文，提供本地线程缓存，请勿在线程间传递
 */
public class MahjongContext {
    // 线程安全，只需要用HashMap就可以了
    private final Map<String, Object> map = new HashMap<>();
    // 把MahjongContext本身放到FastThreadLocal中
    private static final FastThreadLocal<MahjongContext> MAHJONG_CONTEXT = new FastThreadLocal<MahjongContext>() {
        @Override
        protected MahjongContext initialValue() throws Exception {
            return new MahjongContext();
        }
    };

    private MahjongContext() {
    }
    // 当前线程的MahjongContext
    public static MahjongContext currentContext() {
        return MAHJONG_CONTEXT.get();
    }
    // 包装map的put()方法
    private void set(String key, Object value) {
        map.put(key, value);
    }
    // 包装map的get()方法
    private <T> T get(String key) {
        return (T) map.get(key);
    }
    // 包装map的remove()方法
    private void remove(String key) {
        map.remove(key);
    }
    // 设置当前玩家的channel
    public void setCurrentChannel(Channel channel) {
        set("currentChannel", channel);
    }
    // 获取当前玩家的channel
    public Channel getCurrentChannel() {
        return get("currentChannel");
    }

    public void setRequestHeader(MahjongProtocolHeader requestHeader) {
        set("requestHeader", requestHeader);
    }

    public MahjongProtocolHeader getRequestHeader() {
        return get("requestHeader");
    }

    public void setPlayerChannel(Player player, Channel channel) {
        set("playerChannel_" + player.getId(), channel);
    }

    public Channel getPlayerChannel(Player player) {
        return get("playerChannel_" + player.getId());
    }

    public void setChannelRoomId(Channel channel, Long roomId) {
        set("channelRoomId_" + channel.id().asLongText(), roomId);
    }

    public Long getChannelRoomId(Channel channel) {
        return get("channelRoomId_" + channel.id().asLongText());
    }

    public void removeChannelRoomId(Channel channel) {
        remove("channelRoomId_" + channel.id().asLongText());
    }

    public void setCurrentPlayer(Player player) {
        set("currentPlayer", player);
    }

    public Player getCurrentPlayer() {
        return get("currentPlayer");
    }

    public void setCurrentRoom(Room room) {
        set("currentRoom", room);
    }

    public Room getCurrentRoom() {
        return get("currentRoom");
    }

    public void setCurrentRoomId(Long roomId) {
        set("currentRoomId", roomId);
    }

    public Long getCurrentRoomId() {
        return get("currentRoomId");
    }

    public void setRoomById(Room room) {
        set("room_" + room.getId(), room);
    }

    public Room getRoomById(Long id) {
        return get("room_" + id);
    }

    public void removeRoomById(Long id) {
        remove("room_" + id);
    }

    public void setRoomWaitingOperations(Room room, List<OperationNotification> waitingOperationNotifications) {
        set("roomWaitingOperations_" + room.getId(), waitingOperationNotifications);
    }

    public List<OperationNotification> getRoomWaitingOperations(Room room) {
        return get("roomWaitingOperations_" + room.getId());
    }

    public void removeRoomWaitingOperations(Room room) {
        remove("roomWaitingOperations_" + room.getId());
    }

    public void setRoomOperationRequests(Room room, List<OperationRequest> operationRequests) {
        set("roomOperationRequests_" + room.getId(), operationRequests);
    }

    public List<OperationRequest> getRoomOperationRequests(Room room) {
        return get("roomOperationRequests_" + room.getId());
    }

    public void removeRoomOperationRequests(Room room) {
        remove("roomOperationRequests_" + room.getId());
    }
}
