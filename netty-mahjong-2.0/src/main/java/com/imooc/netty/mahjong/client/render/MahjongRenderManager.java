package com.imooc.netty.mahjong.client.render;

import com.google.protobuf.MessageLite;
import com.imooc.netty.mahjong.common.proto.*;

public enum MahjongRenderManager {
    HELLO_RESPONSE_RENDER(HelloResponse.getDefaultInstance(), new HelloResponseRender()),
    LOGIN_RESPONSE_RENDER(LoginResponse.getDefaultInstance(), new LoginResponseRender()),
    CREATE_ROOM_RESPONSE_RENDER(CreateRoomResponse.getDefaultInstance(), new CreateRoomResponseRender()),
    ENTER_ROOM_RESPONSE_RENDER(EnterRoomResponse.getDefaultInstance(), new EnterRoomResponseRender()),
    ROOM_REFRESH_NOTIFICATION_RENDER(RoomRefreshNotification.getDefaultInstance(), new RoomRefreshNotificationRender()),
    OPERATION_NOTIFICATION_RENDER(OperationNotification.getDefaultInstance(), new OperationNotificationRender()),
    OPERATION_RESULT_NOTIFICATION_RENDER(OperationResultNotification.getDefaultInstance(), new OperationResultNotificationRender()),
    GAME_OVER_NOTIFICATION_RENDER(GameOverNotification.getDefaultInstance(), new GameOverNotificationRender()),
    SETTLEMENT_NOTIFICATION_RENDER(SettlementNotification.getDefaultInstance(), new SettlementNotificationRender()),
    ;
    private MessageLite msgType;
    private MahjongRender mahjongRender;

    MahjongRenderManager(MessageLite msgType, MahjongRender mahjongRender) {
        this.msgType = msgType;
        this.mahjongRender = mahjongRender;
    }

    public static MahjongRender choose(MessageLite message) {
        for (MahjongRenderManager value : MahjongRenderManager.values()) {
            if (value.msgType.getClass() == message.getClass()) {
                return value.mahjongRender;
            }
        }
        return null;
    }
}
