package com.imooc.netty.core.test;

public class CloneTest {
    public static void main(String[] args) {
        byte[] cards = new byte[]{1, 2, 3, 4};
        byte[] cloneCards = cards.clone();
        cloneCards[1] = 55;
        System.out.println(cards[1]);
        System.out.println(cloneCards[1]);
    }
}
