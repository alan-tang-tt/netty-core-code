package com.imooc.netty.mahjong.server.processor;

import com.google.protobuf.MessageLite;
import com.imooc.netty.mahjong.common.proto.*;

public enum MahjongProcessorManager {
    HELLO_REQUEST_PROCESSOR(HelloRequest.class, new HelloRequestProcessor()),
    LOGIN_REQUEST_PROCESSOR(LoginRequest.class, new LoginRequestProcessor()),
    CREATE_ROOM_REQUEST_PROCESSOR(CreateRoomRequest.class, new CreateRoomRequestProcessor()),
    ENTER_ROOM_REQUEST_PROCESSOR(EnterRoomRequest.class, new EnterRoomRequestProcessor()),
    OPERATION_REQUEST_REQUEST_PROCESSOR(OperationRequest.class, new OperationRequestProcessor()),
    START_GAME_MESSAGE_PROCESSOR(StartGameMessage.class, new StartGameMessageProcessor()),
    ;

    private Class<? extends MessageLite> msgType;
    private MahjongProcessor mahjongProcessor;

    MahjongProcessorManager(Class<? extends MessageLite> msgType, MahjongProcessor mahjongProcessor) {
        this.msgType = msgType;
        this.mahjongProcessor = mahjongProcessor;
    }

    public static MahjongProcessor choose(MessageLite message) {
        for (MahjongProcessorManager value : MahjongProcessorManager.values()) {
            if (value.msgType == message.getClass()) {
                return value.mahjongProcessor;
            }
        }
        return null;
    }

}
