package com.imooc.netty.core.$26.common.msg;

import com.imooc.netty.core.$26.common.domain.Table;
import com.imooc.netty.core.$26.common.protocol.MahjongNotification;
import lombok.Data;

@Data
public class EnterTableNotification implements MahjongNotification {
    private Table table;
}
