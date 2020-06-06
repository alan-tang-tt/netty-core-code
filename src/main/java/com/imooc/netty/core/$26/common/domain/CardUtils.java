package com.imooc.netty.core.$26.common.domain;

public class CardUtils {
    /**
     * 一个byte可以表示所有牌，高4位代表牌型，低4位代表牌值
     * 牌型：万(1)、条(2)、筒(3)、字（中发白）(4)、风（东南西北）(5)、花（春夏秋冬梅兰竹菊）(6)
     * 牌值：万条筒：123456789，中发白：123，东南西北：1234，春夏秋冬梅兰竹菊：12345678
     */
    private static final byte WAN_MASK = 0x00;
    private static final byte TIAO_MASK = 0x10;
    private static final byte TONG_MASK = 0x20;
    private static final byte ZI_MASK = 0x30;
    private static final byte FENG_MASK = 0x40;
    private static final byte HUA_MASK = 0x50;

    private static final byte COLOR_MASK = 0x70;
    private static final byte VALUE_MASK = 0x0f;

    public static byte color(byte card) {
        return (byte) (card & COLOR_MASK);
    }

    public static byte value(byte card) {
        return (byte) (card & VALUE_MASK);
    }

    public static boolean isWan(byte card) {
        return color(card) == WAN_MASK;
    }

    public static boolean isTiao(byte card) {
        return color(card) == TIAO_MASK;
    }

    public static boolean isTong(byte card) {
        return color(card) == TONG_MASK;
    }

}
