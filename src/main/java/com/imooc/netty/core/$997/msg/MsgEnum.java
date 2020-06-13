package com.imooc.netty.core.$997.msg;

import com.google.protobuf.MessageLite;
import com.imooc.netty.core.$997.proto.LoginRequest;
import com.imooc.netty.core.$997.proto.LoginResponse;

public enum MsgEnum {
    LOGIN_REQUEST(1, LoginRequest.getDefaultInstance()),
    LOGIN_RESPONSE(2, LoginResponse.getDefaultInstance()),
    ;


    private int cmd;
    private MessageLite msg;

    MsgEnum(int cmd, MessageLite msg) {
        this.cmd = cmd;
        this.msg = msg;
    }

    public static MessageLite parse(int cmd) {
        for (MsgEnum msgEnum : MsgEnum.values()) {
            if (msgEnum.cmd == cmd) {
                return msgEnum.msg;
            }
        }
        return null;
    }
}
