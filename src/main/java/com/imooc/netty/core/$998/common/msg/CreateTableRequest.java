package com.imooc.netty.core.$998.common.msg;

import com.imooc.netty.core.$998.common.protocol.MahjongRequest;
import lombok.Data;

@Data
public class CreateTableRequest implements MahjongRequest {
    /**
     * 底注
     */
    private int baseScore;
    /**
     * 玩家最大数量
     */
    private int playerNum;
}
