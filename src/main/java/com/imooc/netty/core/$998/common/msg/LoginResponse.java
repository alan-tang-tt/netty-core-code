package com.imooc.netty.core.$998.common.msg;

import com.imooc.netty.core.$998.common.domain.Player;
import com.imooc.netty.core.$998.common.protocol.MahjongResponse;
import lombok.Data;

@Data
public class LoginResponse implements MahjongResponse {
    private boolean result;
    private Player player;
    private String msg;
}
