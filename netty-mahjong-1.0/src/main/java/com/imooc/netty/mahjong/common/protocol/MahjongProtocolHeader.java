package com.imooc.netty.mahjong.common.protocol;

import io.netty.buffer.ByteBuf;
import lombok.Data;

@Data
public final class MahjongProtocolHeader {
    /**
     * 版本号
     */
    private int version;
    /**
     * 命令字
     */
    private int cmd;
    /**
     * 请求ID
     */
    private int reqId;

    public void decode(ByteBuf msg) {
        version = msg.readInt();
        cmd = msg.readInt();
        reqId = msg.readInt();
    }

    public void encode(ByteBuf buffer) {
        buffer.writeInt(version);
        buffer.writeInt(cmd);
        buffer.writeInt(reqId);
    }
}
