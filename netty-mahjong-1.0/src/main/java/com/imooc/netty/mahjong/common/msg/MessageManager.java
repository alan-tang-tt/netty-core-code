package com.imooc.netty.mahjong.common.msg;

import com.imooc.netty.mahjong.common.protocol.MahjongMessage;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Getter
public enum MessageManager {
    HELLO_REQUEST(1, HelloRequest.class),
    HELLO_RESPONSE(2, HelloResponse.class),
    LOGIN_REQUEST(3, LoginRequest.class),
    LOGIN_RESPONSE(4, LoginResponse.class),
    CREATE_ROOM_REQUEST(5, CreateRoomRequest.class),
    CREATE_ROOM_RESPONSE(6, CreateRoomResponse.class),
    ENTER_ROOM_REQUEST(7, EnterRoomRequest.class),
    ENTER_ROOM_RESPONSE(8, EnterRoomResponse.class),
    ROOM_REFRESH_NOTIFICATION(9, RoomRefreshNotification.class),
    OPERATION_NOTIFICATION(10, OperationNotification.class),
    OPERATION_REQUEST(11, OperationRequest.class),
    OPERATION_RESULT_NOTIFICATION(12, OperationResultNotification.class),
    GAME_OVER_NOTIFICATION(13, GameOverNotification.class),
    SETTLEMENT_NOTIFICATION(14, SettlementNotification.class),
    ;

    private int cmd;
    private Class<? extends MahjongMessage> msgType;

    MessageManager(int cmd, Class<? extends MahjongMessage> msgType) {
        this.cmd = cmd;
        this.msgType = msgType;
    }

    public static Class<? extends MahjongMessage> getMsgTypeByCmd(int cmd) {
        for (MessageManager value : MessageManager.values()) {
            if (value.cmd == cmd) {
                return value.msgType;
            }
        }
        log.error("error cmd: {}", cmd);
        throw new RuntimeException("error cmd:" + cmd);
    }

    public static int getCmdByMsgType(Class<? extends MahjongMessage> msgType) {
        for (MessageManager value : MessageManager.values()) {
            if (value.msgType == msgType) {
                return value.cmd;
            }
        }
        log.error("error msgType: {}", msgType.getName());
        throw new RuntimeException("error msgType: " + msgType.getName());
    }
}
