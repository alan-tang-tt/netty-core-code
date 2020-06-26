package com.imooc.netty.mahjong.common.util;

import com.google.protobuf.MessageLite;
import com.imooc.netty.mahjong.common.proto.*;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum MessageManager {
    HELLO_REQUEST(1, HelloRequest.getDefaultInstance()),
    HELLO_RESPONSE(2, HelloResponse.getDefaultInstance()),
    LOGIN_REQUEST(3, LoginRequest.getDefaultInstance()),
    LOGIN_RESPONSE(4, LoginResponse.getDefaultInstance()),
    CREATE_ROOM_REQUEST(5, CreateRoomRequest.getDefaultInstance()),
    CREATE_ROOM_RESPONSE(6, CreateRoomResponse.getDefaultInstance()),
    ENTER_ROOM_REQUEST(7, EnterRoomRequest.getDefaultInstance()),
    ENTER_ROOM_RESPONSE(8, EnterRoomResponse.getDefaultInstance()),
    ROOM_REFRESH_NOTIFICATION(9, RoomRefreshNotification.getDefaultInstance()),
    OPERATION_NOTIFICATION(10, OperationNotification.getDefaultInstance()),
    OPERATION_REQUEST(11, OperationRequest.getDefaultInstance()),
    OPERATION_RESULT_NOTIFICATION(12, OperationResultNotification.getDefaultInstance()),
    GAME_OVER_NOTIFICATION(13, GameOverNotification.getDefaultInstance()),
    SETTLEMENT_NOTIFICATION(14, SettlementNotification.getDefaultInstance()),
    ;

    private int cmd;
    private MessageLite msgType;

    MessageManager(int cmd, MessageLite msgType) {
        this.cmd = cmd;
        this.msgType = msgType;
    }

    public static MessageLite getMsgTypeByCmd(int cmd) {
        for (MessageManager value : MessageManager.values()) {
            if (value.cmd == cmd) {
                return value.msgType;
            }
        }
        log.error("error cmd: {}", cmd);
        throw new RuntimeException("error cmd:" + cmd);
    }

    public static int getCmdByMsgType(MessageLite msgType) {
        for (MessageManager value : MessageManager.values()) {
            if (value.msgType.getClass() == msgType.getClass()) {
                return value.cmd;
            }
        }
        log.error("error msgType: {}", msgType.getClass().getName());
        throw new RuntimeException("error msgType: " + msgType.getClass().getName());
    }
}
