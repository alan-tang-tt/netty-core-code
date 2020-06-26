package com.imooc.netty.samples.$998.common.msg;

import com.imooc.netty.samples.$998.common.domain.Player;
import com.imooc.netty.samples.$998.common.protocol.MahjongResponse;
import lombok.Data;

@Data
public class LoginResponse implements MahjongResponse {
    private boolean result;
    private Player player;
    private String msg;
}
