package com.imooc.netty.core.test;

public class NormalTest {
    public static void main(String[] args) {
        int idx = log2(8192 >> 13);
        System.out.println(idx);
    }

    private static int log2(int val) {
        int res = 0;
        while (val > 1) {
            val >>= 1;
            res++;
        }
        return res;
    }
}
