package com.imooc.netty.mahjong.common.msg;

import com.imooc.netty.mahjong.common.protocol.MahjongMessage;
import lombok.Data;

@Data
public class HelloRequest implements MahjongMessage {
    private String name;
}
