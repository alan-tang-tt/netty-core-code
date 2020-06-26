package com.imooc.netty.samples.$998.server.data;

import com.imooc.netty.samples.$998.common.domain.Player;
import com.imooc.netty.samples.$998.common.domain.Table;
import lombok.Data;

/**
 * Channel绑定的东西
 */
@Data
public class ChannelBinding {
    private Player player;
    private Table table;
}
