package com.imooc.netty.mahjong.common.msg;

import com.imooc.netty.mahjong.common.protocol.MahjongMessage;
import lombok.Data;

@Data
public class EnterRoomRequest implements MahjongMessage {
    private long roomId;
}
