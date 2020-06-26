package com.imooc.netty.mahjong.client.render;

import com.imooc.netty.mahjong.common.msg.*;
import com.imooc.netty.mahjong.common.protocol.MahjongMessage;

public enum MahjongRenderManager {
    HELLO_RESPONSE_RENDER(HelloResponse.class, new HelloResponseRender()),
    LOGIN_RESPONSE_RENDER(LoginResponse.class, new LoginResponseRender()),
    CREATE_ROOM_RESPONSE_RENDER(CreateRoomResponse.class, new CreateRoomResponseRender()),
    ENTER_ROOM_RESPONSE_RENDER(EnterRoomResponse.class, new EnterRoomResponseRender()),
    ROOM_REFRESH_NOTIFICATION_RENDER(RoomRefreshNotification.class, new RoomRefreshNotificationRender()),
    OPERATION_NOTIFICATION_RENDER(OperationNotification.class, new OperationNotificationRender()),
    OPERATION_RESULT_NOTIFICATION_RENDER(OperationResultNotification.class, new OperationResultNotificationRender()),
    GAME_OVER_NOTIFICATION_RENDER(GameOverNotification.class, new GameOverNotificationRender()),
    SETTLEMENT_NOTIFICATION_RENDER(SettlementNotification.class, new SettlementNotificationRender()),
    ;
    private Class<? extends MahjongMessage> msgType;
    private MahjongRender mahjongRender;

    MahjongRenderManager(Class<? extends MahjongMessage> msgType, MahjongRender mahjongRender) {
        this.msgType = msgType;
        this.mahjongRender = mahjongRender;
    }

    public static MahjongRender choose(MahjongMessage message) {
        for (MahjongRenderManager value : MahjongRenderManager.values()) {
            if (value.msgType == message.getClass()) {
                return value.mahjongRender;
            }
        }
        return null;
    }
}
