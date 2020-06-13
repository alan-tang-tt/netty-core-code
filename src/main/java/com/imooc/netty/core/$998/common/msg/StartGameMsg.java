package com.imooc.netty.core.$998.common.msg;

import com.imooc.netty.core.$998.common.domain.Table;
import com.imooc.netty.core.$998.common.protocol.MahjongMsg;
import lombok.Data;

@Data
public class StartGameMsg implements MahjongMsg {
    private Table table;
}
