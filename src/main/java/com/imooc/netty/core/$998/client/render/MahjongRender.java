package com.imooc.netty.core.$998.client.render;

import com.imooc.netty.core.$998.common.protocol.MahjongResponse;

public interface MahjongRender<T extends MahjongResponse> {
    void render(T response);
}
