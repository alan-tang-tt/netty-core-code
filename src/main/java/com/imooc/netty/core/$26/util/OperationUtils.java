package com.imooc.netty.core.$26.util;

public class OperationUtils {
    /**
     * 出、吃、碰、杠、胡
     * 多个操作可使用一个变量表示，比如同时可碰可杠，则用 OPERATION_PENG | OPERATION_GANG 表示即可。
     */
    public static final int OPERATION_CHU = 1;
    public static final int OPERATION_CHI = 2;
    public static final int OPERATION_PENG = 4;
    public static final int OPERATION_GANG = 8;
    public static final int OPERATION_HU = 16;

    /**
     * 操作倒计时
     */
    public static final int OPERATION_DEPLAY_TIME = 15;

}
