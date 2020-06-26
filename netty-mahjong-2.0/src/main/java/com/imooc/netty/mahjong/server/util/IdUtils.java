package com.imooc.netty.mahjong.server.util;

import java.util.concurrent.atomic.AtomicLong;

public class IdUtils {
    private static final AtomicLong ID_GENERATOR = new AtomicLong(1);

    public static long generateId() {
        return ID_GENERATOR.getAndIncrement();
    }
}
