package com.imooc.netty.core.$26.common.msg;

import com.alibaba.fastjson.JSON;
import com.imooc.netty.core.$26.common.domain.Table;
import com.imooc.netty.core.$26.common.protocol.MahjongNotification;
import lombok.Data;

/**
 * 桌子通知，用于全量刷新桌子信息
 */
@Data
public class TableNotification implements MahjongNotification, Cloneable {
    private Table table;

    @Override
    public TableNotification clone() {
        // 深拷贝
        return JSON.parseObject(JSON.toJSONString(this), TableNotification.class);
    }
}
