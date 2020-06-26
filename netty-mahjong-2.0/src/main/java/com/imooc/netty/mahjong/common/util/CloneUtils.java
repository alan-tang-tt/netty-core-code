package com.imooc.netty.mahjong.common.util;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CloneUtils {
    public static <T extends MessageLite> T clone(T msg) {
        byte[] bytes = msg.toByteArray();
        try {
            return (T) msg.getParserForType().parseFrom(bytes);
        } catch (InvalidProtocolBufferException e) {
            log.error("clone error", e);
            throw new RuntimeException("clone error", e);
        }
    }
}
