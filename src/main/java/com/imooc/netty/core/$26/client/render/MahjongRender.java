package com.imooc.netty.core.$26.client.render;

import com.imooc.netty.core.$26.common.protocol.MahjongResponse;

public interface MahjongRender<T extends MahjongResponse> {
    void render(T response);
}
