package com.imooc.netty.mahjong.common.util;

import com.google.protobuf.ByteString;
import com.google.protobuf.MessageLite;
import com.imooc.netty.mahjong.common.domain.Player;
import com.imooc.netty.mahjong.common.domain.Room;
import com.imooc.netty.mahjong.common.proto.OperationNotification;
import com.imooc.netty.mahjong.common.proto.PlayerMsg;
import com.imooc.netty.mahjong.common.proto.RoomMsg;
import com.imooc.netty.mahjong.common.proto.RoomRefreshNotification;
import com.imooc.netty.mahjong.common.protocol.MahjongProtocol;
import com.imooc.netty.mahjong.common.protocol.MahjongProtocolHeader;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.util.concurrent.Future;
import io.netty.util.concurrent.GenericFutureListener;
import io.netty.util.concurrent.Promise;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class MessageUtils {
    private static final int DEFAULT_VERSION = 1;
    private static final AtomicInteger REQ_ID = new AtomicInteger(1);

    public static void sendRequest(MessageLite request) {
        MahjongProtocol mahjongProtocol = new MahjongProtocol();

        MahjongProtocolHeader header = new MahjongProtocolHeader();
        header.setVersion(DEFAULT_VERSION);
        header.setReqId(REQ_ID.getAndIncrement());
        header.setCmd(MessageManager.getCmdByMsgType(request));

        mahjongProtocol.setHeader(header);
        mahjongProtocol.setBody(request);

        Channel channel = MahjongContext.currentContext().getCurrentChannel();

        send(channel, mahjongProtocol);
    }

    public static void sendResponse(MessageLite response) {
        MahjongProtocol mahjongProtocol = new MahjongProtocol();

        MahjongProtocolHeader requestHeader = MahjongContext.currentContext().getRequestHeader();
        MahjongProtocolHeader responseHeader = new MahjongProtocolHeader();
        responseHeader.setVersion(requestHeader.getVersion());
        responseHeader.setReqId(requestHeader.getReqId());
        responseHeader.setCmd(MessageManager.getCmdByMsgType(response));

        mahjongProtocol.setHeader(responseHeader);
        mahjongProtocol.setBody(response);

        Channel channel = MahjongContext.currentContext().getCurrentChannel();

        send(channel, mahjongProtocol);
    }

    public static void sendNotification(Room room, MessageLite notification) {
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

    private static void sendNotification0(Room room, MessageLite notification) {
        MahjongProtocol mahjongProtocol = buildNotification(notification);

        // 通知是需要发送给所有玩家的
        for (Player player : room.getPlayers()) {
            if (player != null) {
                Channel channel = MahjongContext.currentContext().getPlayerIdChannel(player.getId());
                send(channel, mahjongProtocol);
            }
        }
    }

    private static MahjongProtocol buildNotification(MessageLite notification) {
        MahjongProtocol mahjongProtocol = new MahjongProtocol();

        MahjongProtocolHeader header = new MahjongProtocolHeader();
        header.setVersion(DEFAULT_VERSION);
        header.setReqId(REQ_ID.getAndIncrement());
        header.setCmd(MessageManager.getCmdByMsgType(notification));

        mahjongProtocol.setHeader(header);
        mahjongProtocol.setBody(notification);
        return mahjongProtocol;
    }

    public static void sendRoomRefreshNotification(RoomRefreshNotification roomRefreshNotification) {
        // 克隆一个对象出来，防止房间信息变化时，消息还没发出去
        roomRefreshNotification = CloneUtils.clone(roomRefreshNotification);
        // 隐藏房间未摸的牌
        RoomMsg room = roomRefreshNotification.getRoom();
        byte[] remainCards = room.getRemainCards().toByteArray();
        if (remainCards != null) {
            for (int i = 0; i < remainCards.length; i++) {
                if (remainCards[i] != 0) {
                    // 全部变成1万，哈哈
                    remainCards[i] = 1;
                }
            }
        }
        roomRefreshNotification = roomRefreshNotification.toBuilder().setRoom(room.toBuilder().setRemainCards(ByteString.copyFrom(remainCards)).build()).build();

        // 发送给所有玩家
        for (PlayerMsg player : room.getPlayersList()) {
            if (player != null) {
                send2Player(player, roomRefreshNotification);
            }
        }
    }

    private static void send2Player(PlayerMsg toPlayer, RoomRefreshNotification roomRefreshNotification) {
        roomRefreshNotification = CloneUtils.clone(roomRefreshNotification);
        RoomMsg room = roomRefreshNotification.getRoom();

        // 隐藏其他玩家的牌
        List<PlayerMsg> newPlayersList = new ArrayList<>();
        List<PlayerMsg> playersList = room.getPlayersList();
        for (PlayerMsg playerMsg : playersList) {
            if (playerMsg != null && playerMsg.getId() != toPlayer.getId()) {
                byte[] cards = playerMsg.getCards().toByteArray();
                if (cards != null) {
                    for (int i = 0; i < cards.length; i++) {
                        if (cards[i] != 0) {
                            // 全部变成1万，哈哈
                            cards[i] = 0x11;
                        }
                    }
                }
                playerMsg = playerMsg.toBuilder().setCards(ByteString.copyFrom(cards)).build();
            }
            newPlayersList.add(playerMsg);
        }

        for (PlayerMsg newPlayerMsg : newPlayersList) {
            for (int i = 0; i < playersList.size(); i++) {
                if (newPlayerMsg.getId() == playersList.get(i).getId()) {
                    room = room.toBuilder().setPlayers(i, newPlayerMsg).build();
                }
            }
        }
        roomRefreshNotification = roomRefreshNotification.toBuilder().setRoom(room).build();

        MahjongProtocol mahjongProtocol = buildNotification(roomRefreshNotification);
        Channel channel = MahjongContext.currentContext().getPlayerIdChannel(toPlayer.getId());
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
        header.setCmd(MessageManager.getCmdByMsgType(operationNotification));

        mahjongProtocol.setHeader(header);
        mahjongProtocol.setBody(operationNotification);

        Channel channel = MahjongContext.currentContext().getPlayerIdChannel(player.getId());

        send(channel, mahjongProtocol);
    }

    private static void send(Channel channel, MahjongProtocol mahjongProtocol) {
        if (channel != null && channel.isActive() && channel.isWritable()) {
            ChannelFuture channelFuture = channel.writeAndFlush(mahjongProtocol);
            // 添加监听器，发送失败时打印日志
            channelFuture.addListener(future -> {
                if (!future.isSuccess()) {
                    log.error("send message error", future.cause());
                }
            });
        } else {
            log.error("channel unavailable, channelId={}, msgType={}", channel.id(), ((MessageLite) mahjongProtocol.getBody()).getClass());
        }
    }
}
