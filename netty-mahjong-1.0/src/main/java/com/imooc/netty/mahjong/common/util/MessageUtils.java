package com.imooc.netty.mahjong.common.util;

import com.alibaba.fastjson.JSON;
import com.imooc.netty.mahjong.common.domain.Player;
import com.imooc.netty.mahjong.common.domain.Room;
import com.imooc.netty.mahjong.common.msg.MessageManager;
import com.imooc.netty.mahjong.common.msg.OperationNotification;
import com.imooc.netty.mahjong.common.msg.RoomRefreshNotification;
import com.imooc.netty.mahjong.common.protocol.MahjongMessage;
import com.imooc.netty.mahjong.common.protocol.MahjongProtocol;
import com.imooc.netty.mahjong.common.protocol.MahjongProtocolHeader;
import io.netty.channel.Channel;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class MessageUtils {
    private static final int DEFAULT_VERSION = 1;
    private static final AtomicInteger REQ_ID = new AtomicInteger(1);

    public static void sendRequest(MahjongMessage request) {
        MahjongProtocol mahjongProtocol = new MahjongProtocol();

        MahjongProtocolHeader header = new MahjongProtocolHeader();
        header.setVersion(DEFAULT_VERSION);
        header.setReqId(REQ_ID.getAndIncrement());
        header.setCmd(MessageManager.getCmdByMsgType(request.getClass()));

        mahjongProtocol.setHeader(header);
        mahjongProtocol.setBody(request);

        Channel channel = MahjongContext.currentContext().getCurrentChannel();

        send(channel, mahjongProtocol);
    }

    public static void sendResponse(MahjongMessage response) {
        MahjongProtocol mahjongProtocol = new MahjongProtocol();

        MahjongProtocolHeader requestHeader = MahjongContext.currentContext().getRequestHeader();
        MahjongProtocolHeader responseHeader = new MahjongProtocolHeader();
        responseHeader.setVersion(requestHeader.getVersion());
        responseHeader.setReqId(requestHeader.getReqId());
        responseHeader.setCmd(MessageManager.getCmdByMsgType(response.getClass()));

        mahjongProtocol.setHeader(responseHeader);
        mahjongProtocol.setBody(response);

        Channel channel = MahjongContext.currentContext().getCurrentChannel();

        send(channel, mahjongProtocol);
    }

    public static void sendNotification(Room room, MahjongMessage notification) {
        // 防止用错方法，加一层防护
        if (notification instanceof RoomRefreshNotification) {
            sendRoomRefreshNotification((RoomRefreshNotification) notification);
            return;
        }
        if (notification instanceof OperationNotification) {
            sendOperationNotification(room, (OperationNotification) notification, Collections.emptyList());
            return;
        }
        sendNotification0(room, notification);
    }

    private static void sendNotification0(Room room, MahjongMessage notification) {
        MahjongProtocol mahjongProtocol = buildNotification(notification);

        // 通知是需要发送给所有玩家的
        Player[] players = room.getPlayers();
        for (Player player : players) {
            if (player != null) {
                Channel channel = MahjongContext.currentContext().getPlayerChannel(player);
                send(channel, mahjongProtocol);
            }
        }
    }

    private static MahjongProtocol buildNotification(MahjongMessage notification) {
        MahjongProtocol mahjongProtocol = new MahjongProtocol();

        MahjongProtocolHeader header = new MahjongProtocolHeader();
        header.setVersion(DEFAULT_VERSION);
        header.setReqId(REQ_ID.getAndIncrement());
        header.setCmd(MessageManager.getCmdByMsgType(notification.getClass()));

        mahjongProtocol.setHeader(header);
        mahjongProtocol.setBody(notification);
        return mahjongProtocol;
    }

    public static void sendRoomRefreshNotification(RoomRefreshNotification roomRefreshNotification) {
        // 克隆一个对象出来，防止房间信息变化时，消息还没发出去
        roomRefreshNotification = JSON.parseObject(JSON.toJSONString(roomRefreshNotification), RoomRefreshNotification.class);
        // 隐藏房间未摸的牌
        Room room = roomRefreshNotification.getRoom();
        byte[] remainCards = room.getRemainCards();
        if (remainCards != null) {
            for (int i = 0; i < remainCards.length; i++) {
                if (remainCards[i] != 0) {
                    // 全部变成1万，哈哈
                    remainCards[i] = 1;
                }
            }
        }

        // 发送给所有玩家
        Player[] players = room.getPlayers();
        for (Player player : players) {
            if (player != null) {
                send2Player(player, roomRefreshNotification);
            }
        }
    }

    private static void send2Player(Player toPlayer, RoomRefreshNotification roomRefreshNotification) {
        roomRefreshNotification = JSON.parseObject(JSON.toJSONString(roomRefreshNotification), RoomRefreshNotification.class);
        Room room = roomRefreshNotification.getRoom();

        // 隐藏其他玩家的牌
        for (Player player : room.getPlayers()) {
            if (player != null && player.getId() != toPlayer.getId()) {
                byte[] cards = player.getCards();
                if (cards != null) {
                    for (int i = 0; i < cards.length; i++) {
                        if (cards[i] != 0) {
                            // 全部变成1万，哈哈
                            cards[i] = 0x11;
                        }
                    }
                }
            }
        }

        MahjongProtocol mahjongProtocol = buildNotification(roomRefreshNotification);
        Channel channel = MahjongContext.currentContext().getPlayerChannel(toPlayer);
        send(channel, mahjongProtocol);
    }

    public static void sendOperationNotification(Room room, OperationNotification operationNotification, List<Player> exceptPlayers) {
        for (Player player : room.getPlayers()) {
            if (player != null && !exceptPlayers.contains(player)) {
                sendOperationNotification(player, operationNotification);
            }
        }
    }

    public static void sendOperationNotification(Player player, OperationNotification operationNotification) {
        MahjongProtocol mahjongProtocol = new MahjongProtocol();

        MahjongProtocolHeader header = new MahjongProtocolHeader();
        header.setVersion(DEFAULT_VERSION);
        header.setReqId(REQ_ID.getAndIncrement());
        header.setCmd(MessageManager.getCmdByMsgType(OperationNotification.class));

        mahjongProtocol.setHeader(header);
        mahjongProtocol.setBody(operationNotification);

        Channel channel = MahjongContext.currentContext().getPlayerChannel(player);

        send(channel, mahjongProtocol);
    }

    private static void send(Channel channel, MahjongProtocol mahjongProtocol) {
        if (channel != null && channel.isActive() && channel.isWritable()) {
            channel.writeAndFlush(mahjongProtocol);
        } else {
            log.error("channel unavailable, channelId={}, msgType={}", channel.id(), ((MahjongMessage) mahjongProtocol.getBody()).getClass());
        }
    }
}
