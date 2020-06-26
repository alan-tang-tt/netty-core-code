package com.imooc.netty.mahjong.server.processor;

import com.imooc.netty.mahjong.common.msg.HelloRequest;
import com.imooc.netty.mahjong.common.msg.HelloResponse;
import com.imooc.netty.mahjong.common.util.MessageUtils;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HelloRequestProcessor implements MahjongProcessor<HelloRequest> {

    @Override
    public void process(HelloRequest message) {
        log.info("receive hello request: {}", message);
        HelloResponse response = new HelloResponse();
        response.setMessage("你好，" + message.getName());
        MessageUtils.sendResponse(response);
    }
}
