package com.imooc.netty.core.$998.common.msg;

import com.imooc.netty.core.$998.common.domain.Table;
import com.imooc.netty.core.$998.common.protocol.MahjongResponse;
import lombok.Data;

@Data
public class EnterTableResponse implements MahjongResponse {
    private boolean result;
    private Table table;
    private String msg;
}
