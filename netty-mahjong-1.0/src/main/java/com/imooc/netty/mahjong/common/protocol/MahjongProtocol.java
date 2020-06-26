package com.imooc.netty.mahjong.common.protocol;

import com.alibaba.fastjson.JSON;
import com.imooc.netty.mahjong.common.msg.MessageManager;
import io.netty.buffer.ByteBuf;
import lombok.Data;

import java.nio.charset.StandardCharsets;

@Data
public final class MahjongProtocol {
    /**
     * 协议头
     */
    private MahjongProtocolHeader header;
    /**
     * 协议体
     */
    private MahjongProtocolBody body;

    public void decode(ByteBuf msg) {
        MahjongProtocolHeader header = new MahjongProtocolHeader();
        // 解码header
        header.decode(msg);
        this.header = header;

        // 命令字
        int cmd = header.getCmd();
        // 根据命令字获取body的真实类型
        Class<? extends MahjongProtocolBody> bodyType = getBodyTypeByCmd(cmd);
        this.body = JSON.parseObject(msg.toString(StandardCharsets.UTF_8), bodyType);
    }

    private Class<? extends MahjongProtocolBody> getBodyTypeByCmd(int cmd) {
        return MessageManager.getMsgTypeByCmd(cmd);
    }

    public void encode(ByteBuf buffer) {
        header.encode(buffer);
        buffer.writeBytes(JSON.toJSONString(body).getBytes(StandardCharsets.UTF_8));
    }
}
