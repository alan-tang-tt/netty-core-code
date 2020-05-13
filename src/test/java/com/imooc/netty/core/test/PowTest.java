package com.imooc.netty.core.test;

public class PowTest {
    public static void main(String[] args) {
        System.out.println(nearestPow(3));
        System.out.println(nearestPow(8));
        System.out.println(nearestPow(10));
        System.out.println(nearestPow(17));
    }

    private static int nearestPow(int index) {
        int newCapacity = index;
        newCapacity |= newCapacity >>>  1;
        newCapacity |= newCapacity >>>  2;
        newCapacity |= newCapacity >>>  4;
        newCapacity |= newCapacity >>>  8;
        newCapacity |= newCapacity >>> 16;
        newCapacity ++;
        return newCapacity;
    }
}
