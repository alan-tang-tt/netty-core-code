package com.imooc.netty.core.$26.common.msg;

import com.imooc.netty.core.$26.common.domain.Table;
import com.imooc.netty.core.$26.common.protocol.MahjongMsg;
import lombok.Data;

@Data
public class StartGameMsg implements MahjongMsg {
    private Table table;
}
