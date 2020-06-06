package com.imooc.netty.core.$26.server.processor;

import com.imooc.netty.core.$26.common.protocol.MahjongMsg;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public interface MahjongProcessor<T extends MahjongMsg> {
    void process(T msg);

    static void processMsg(MahjongMsg msg) {
        MahjongProcessor processor = ProcessorEnum.getProcessor(msg.getClass());
        if (processor != null) {
            processor.process(msg);
        } else {
            Logger log = LoggerFactory.getLogger(MahjongProcessor.class);
            log.error("not found request processor, requestType={}", msg.getClass().getSimpleName());
        }
    }
}
