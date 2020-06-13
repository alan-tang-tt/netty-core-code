package com.imooc.netty.core.$998.common.msg;

import com.alibaba.fastjson.JSON;
import com.imooc.netty.core.$998.common.domain.Table;
import com.imooc.netty.core.$998.common.protocol.MahjongNotification;
import lombok.Data;

/**
 * 桌子通知，用于全量刷新桌子信息
 */
@Data
public class TableNotification implements MahjongNotification, Cloneable {
    private Table table;
    /**
     * 什么操作导致了刷新桌子
     */
    private int operation;

    @Override
    public TableNotification clone() {
        // 深拷贝
        return JSON.parseObject(JSON.toJSONString(this), TableNotification.class);
    }
}
