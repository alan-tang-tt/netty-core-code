package com.imooc.netty.samples.$998.client.render;

import com.imooc.netty.samples.$998.common.protocol.MahjongResponse;

public interface MahjongRender<T extends MahjongResponse> {
    void render(T response);
}
