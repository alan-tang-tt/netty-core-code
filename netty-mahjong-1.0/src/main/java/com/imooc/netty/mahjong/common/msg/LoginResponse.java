package com.imooc.netty.mahjong.common.msg;

import com.imooc.netty.mahjong.common.domain.Player;
import com.imooc.netty.mahjong.common.protocol.MahjongMessage;
import lombok.Data;

@Data
public class LoginResponse implements MahjongMessage {
    private boolean result;
    private Player player;
    private String message;
}
