package com.imooc.netty.core.$26.common.domain;

import io.netty.channel.Channel;
import lombok.Data;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

@Data
public class Player {
    /**
     * 玩家id
     */
    private long id;
    /**
     * 玩家昵称
     */
    private String name;
    /**
     * 玩家积分
     */
    private int score;
    /**
     * 玩家钻石
     */
    private int diamond;
    /**
     * 玩家拥有的牌
     */
    private byte[] cards;

    public int validCardNum() {
        int num = 0;
        for (byte card : cards) {
            if (card != 0) {
                num++;
            }
        }
        return num;
    }

    public byte lastCard() {
        for (int i = cards.length - 1; i >= 0; i--) {
            if (cards[i] !=0) {
                return cards[i];
            }
        }
        return 0;
    }

    public boolean containCards(byte...cards) {
        // 克隆一份，不影响原数据
        byte[] cloneCards = this.cards.clone();
        outer:for (byte card : cards) {
            for (int i = 0; i < cloneCards.length; i++) {
                if (cloneCards[i] == card) {
                    // 找到了就归0，不影响多张一样牌的情况
                    cloneCards[i] = 0;
                    continue outer;
                }
            }
            return false;
        }
        return true;
    }

    public void removeCard(byte...removedCards) {
        for (byte removedCard : removedCards) {
            for (int i = 0; i < this.cards.length; i++) {
                if (this.cards[i] == removedCard) {
                    this.cards[i] = 0;
                    break;
                }
            }
        }
    }
}
