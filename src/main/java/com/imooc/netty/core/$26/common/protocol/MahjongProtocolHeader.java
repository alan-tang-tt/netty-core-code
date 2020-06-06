package com.imooc.netty.core.$26.common.protocol;

import lombok.Data;

@Data
public class MahjongProtocolHeader {
    private int version;
    private int cmd;
    private long reqId;
}
