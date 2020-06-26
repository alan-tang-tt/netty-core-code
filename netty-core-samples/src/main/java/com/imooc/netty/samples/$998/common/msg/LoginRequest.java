package com.imooc.netty.samples.$998.common.msg;

import com.imooc.netty.samples.$998.common.protocol.MahjongRequest;
import lombok.Data;

@Data
public class LoginRequest implements MahjongRequest {
    private String username;
    private String password;
}
