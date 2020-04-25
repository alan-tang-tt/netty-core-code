package com.imooc.netty.core.$17;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.UnpooledByteBufAllocator;

public class ByteBufTest {
    public static void main(String[] args) {
        // 参数是preferDirect，即是否偏向于使用直接内存
        UnpooledByteBufAllocator allocator = new UnpooledByteBufAllocator(false);

        // 创建一个非池化基于堆内存的ByteBuf
        ByteBuf byteBuf = allocator.heapBuffer();

        // 写入数据
        byteBuf.writeInt(1);
        byteBuf.writeInt(2);
        byteBuf.writeInt(3);

        // 读取数据
        System.out.println(byteBuf.readInt());
        System.out.println(byteBuf.readInt());
        System.out.println(byteBuf.readInt());
    }
}
