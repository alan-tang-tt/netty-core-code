package com.imooc.netty.mahjong.common.protocol;

import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.imooc.netty.mahjong.common.util.MessageManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.Data;

@Data
public final class MahjongProtocol {
    /**
     * 协议头
     */
    private MahjongProtocolHeader header;
    /**
     * 协议体
     */
    private MessageLite body;

    public void decode(ByteBuf msg) throws InvalidProtocolBufferException {
        MahjongProtocolHeader header = new MahjongProtocolHeader();
        // 解码header
        header.decode(msg);
        this.header = header;

        // 命令字
        int cmd = header.getCmd();
        // 根据命令字获取body的真实类型
        MessageLite msgType = getBodyTypeByCmd(cmd);

        // 解码body
        final byte[] array;
        final int offset;
        final int length = msg.readableBytes();
        if (msg.hasArray()) {
            array = msg.array();
            offset = msg.arrayOffset() + msg.readerIndex();
        } else {
            array = ByteBufUtil.getBytes(msg, msg.readerIndex(), length, false);
            offset = 0;
        }

        this.body = msgType.getParserForType().parseFrom(array, offset, length);
    }

    private MessageLite getBodyTypeByCmd(int cmd) {
        return MessageManager.getMsgTypeByCmd(cmd);
    }

    public void encode(ByteBuf buffer) {
        header.encode(buffer);
        buffer.writeBytes(body.toByteArray());
    }
}
