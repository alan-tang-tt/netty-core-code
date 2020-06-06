package com.imooc.netty.core.$26.client.render;

import com.imooc.netty.core.$26.common.msg.LoginResponse;
import com.imooc.netty.core.$26.common.protocol.MahjongResponse;

import java.util.HashMap;
import java.util.Map;

/**
 * 渲染器，可以为单例
 */
public enum RenderEnum {
    LOGIN_RESPONSE_RENDER(LoginResponse.class, new LoginResponseRender()),
    ;

    private static Map<Class<? extends MahjongResponse>, MahjongRender> cache;
    private Class<? extends MahjongResponse> responseType;
    private MahjongRender render;

    RenderEnum(Class<? extends MahjongResponse> responseType, MahjongRender render) {
        this.responseType = responseType;
        this.render = render;
    }

    public static MahjongRender getRender(Class<? extends MahjongResponse> responseType) {
        checkIfInitCache();
        return cache.get(responseType);
    }

    private static void checkIfInitCache() {
        if (RenderEnum.cache == null) {
            synchronized (RenderEnum.class) {
                if (RenderEnum.cache == null) {
                    Map<Class<? extends MahjongResponse>, MahjongRender> cache = new HashMap<>();
                    for (RenderEnum renderEnum : RenderEnum.values()) {
                        cache.put(renderEnum.responseType, renderEnum.render);
                    }
                    RenderEnum.cache = cache;
                }
            }
        }

    }
}
