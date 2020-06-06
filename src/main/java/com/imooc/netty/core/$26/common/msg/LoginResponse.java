package com.imooc.netty.core.$26.common.msg;

import com.imooc.netty.core.$26.common.domain.Player;
import com.imooc.netty.core.$26.common.protocol.MahjongResponse;
import lombok.Data;

@Data
public class LoginResponse implements MahjongResponse {
    private boolean result;
    private Player player;
    private String msg;
}
