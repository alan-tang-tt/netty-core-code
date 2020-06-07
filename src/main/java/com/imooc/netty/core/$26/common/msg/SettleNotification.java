package com.imooc.netty.core.$26.common.msg;

import com.imooc.netty.core.$26.common.protocol.MahjongNotification;
import lombok.Data;

@Data
public class SettleNotification implements MahjongNotification {
    private int winnerPos;
    private int baseScore;
}
