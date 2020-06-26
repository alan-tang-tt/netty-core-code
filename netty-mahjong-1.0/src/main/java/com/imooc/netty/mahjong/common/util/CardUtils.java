package com.imooc.netty.mahjong.common.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CardUtils {
    /**
     * 万的掩码，一万到九万：0x11~0x19
     */
    public static final byte CARD_TYPE_WAN_MASK = 0x10;
    /**
     * 条的掩码，一条到九条：0x21~0x29
     */
    public static final byte CARD_TYPE_TIAO_MASK = 0x20;
    /**
     * 筒的掩码，一筒到九筒：0x31~0x39
     */
    public static final byte CARD_TYPE_TONG_MASK = 0x30;

    /**
     * 类型的掩码
     */
    public static final byte CARD_TYPE_MASK = 0x70;
    /**
     * 值的掩码
     */
    public static final byte CARD_VALUE_MASK = 0x0f;

    /**
     * 获取牌的类型
     * @param card
     * @return
     */
    public static final byte cardType(byte card) {
        return (byte) (CARD_TYPE_MASK & card);
    }

    /**
     * 获取牌的数值
     * @param card
     * @return
     */
    public static final byte cardValue(byte card) {
        return (byte) (CARD_VALUE_MASK & card);
    }

    public static byte[] initCard() {
        List<Byte> list = new ArrayList<>();
        for (int i = 1; i <= 9; i++) {
            for (int j = 0; j < 4; j++) {
                list.add((byte) (CARD_TYPE_WAN_MASK | i));
                list.add((byte) (CARD_TYPE_TIAO_MASK | i));
                list.add((byte) (CARD_TYPE_TONG_MASK | i));
            }
        }

        // 洗牌，多洗几次
        Collections.shuffle(list);
        Collections.shuffle(list);
        Collections.shuffle(list);
        Collections.shuffle(list);

        byte[] cards = new byte[list.size()];
        for (int i = 0; i < list.size(); i++) {
            cards[i] = list.get(i);
        }

        return cards;
    }
}
