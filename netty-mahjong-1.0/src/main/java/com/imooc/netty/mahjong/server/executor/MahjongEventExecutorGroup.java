package com.imooc.netty.mahjong.server.executor;

import com.imooc.netty.mahjong.common.domain.Player;
import com.imooc.netty.mahjong.common.msg.CreateRoomRequest;
import com.imooc.netty.mahjong.common.msg.EnterRoomRequest;
import com.imooc.netty.mahjong.common.protocol.MahjongMessage;
import com.imooc.netty.mahjong.common.protocol.MahjongProtocol;
import com.imooc.netty.mahjong.common.protocol.MahjongProtocolHeader;
import com.imooc.netty.mahjong.common.util.MahjongContext;
import com.imooc.netty.mahjong.server.data.DataManager;
import com.imooc.netty.mahjong.server.processor.MahjongProcessor;
import com.imooc.netty.mahjong.server.processor.MahjongProcessorManager;
import com.imooc.netty.mahjong.server.util.IdUtils;
import io.netty.channel.Channel;
import io.netty.util.NettyRuntime;
import io.netty.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Executor;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
public class MahjongEventExecutorGroup extends MultithreadEventExecutorGroup {

    private static final MahjongEventExecutorGroup INSTANCE = new MahjongEventExecutorGroup(NettyRuntime.availableProcessors());

    private MahjongEventExecutorGroup(int nThreads) {
        super(nThreads, null, new MahjongEventExecutorChooserFactory(), null);
    }

    @Override
    public void execute(Runnable command) {
        EventExecutor executor = next();

        // 创建一个promise监听任务执行的结果
        Promise<?> promise = executor.newPromise();
        promise.addListener(p -> {
            if (!p.isSuccess()) {
                // 执行失败，打印日志
                // 可以通过自定义异常，做一些失败统计等等
                log.error("execute error", p.cause());
            }
        });

        executor.execute(() -> {
            try {
                command.run();
                promise.setSuccess(null);
            } catch (Exception e) {
                promise.setFailure(e);
            }
        });
    }

    @Override
    protected EventExecutor newChild(Executor executor, Object... args) throws Exception {
        return new DefaultEventExecutor(this, executor);
    }

    public static void execute(Channel channel, MahjongProtocol mahjongProtocol) {
        // 协议头
        MahjongProtocolHeader header = mahjongProtocol.getHeader();
        // 协议体
        MahjongMessage message = (MahjongMessage) mahjongProtocol.getBody();
        // 创建房间和加入房间需要做一些处理
        Long roomId;
        if (message instanceof CreateRoomRequest) {
            // 如果是创建房间消息，生成一个roomId，并将当前channel与之绑定
            roomId = IdUtils.generateId();
            // 一个channel一旦建立，始终与一个eventLoop绑定
            // 所以，可以把channel与房间号的绑定放在线程本地缓存中
            MahjongContext.currentContext().setChannelRoomId(channel, roomId);
        } else if (message instanceof EnterRoomRequest) {
            // 如果是加入房间消息，将当前channel与传入的tableId绑定
            EnterRoomRequest enterRoomRequest = (EnterRoomRequest) message;
            roomId = enterRoomRequest.getRoomId();
            MahjongContext.currentContext().setChannelRoomId(channel, roomId);
        } else {
            // 其它消息则从context中获取当前channel对应的房间号
            // 当然，也可能没有，比如登录请求
            roomId = MahjongContext.currentContext().getChannelRoomId(channel);
        }

        // 设置房间id到context中，以便下面的next()方法可以取到
        MahjongContext.currentContext().setCurrentRoomId(roomId);
        // 将消息扔到业务线程池中处理
        INSTANCE.execute(() -> {
            // 已经切换线程，重新设置房间号到context中
            MahjongContext.currentContext().setCurrentRoomId(roomId);
            // 设置channel等其它线程本地变量
            MahjongContext.currentContext().setCurrentChannel(channel);
            MahjongContext.currentContext().setChannelRoomId(channel, roomId);
            MahjongContext.currentContext().setRequestHeader(header);
            MahjongContext.currentContext().setCurrentRoom(MahjongContext.currentContext().getRoomById(roomId));

            Player currentPlayer = DataManager.getChannelPlayer(channel);
            if (currentPlayer != null) {
                MahjongContext.currentContext().setCurrentPlayer(currentPlayer);
                MahjongContext.currentContext().setPlayerChannel(currentPlayer, channel);
            }
            // 寻找处理器
            MahjongProcessor processor = MahjongProcessorManager.choose(message);
            if (processor != null) {
                // 交给处理器处理
                processor.process(message);
            } else {
                throw new RuntimeException("not found processor, msgType=" + message.getClass().getName());
            }
        });

    }

    private static class MahjongEventExecutorChooserFactory implements EventExecutorChooserFactory {

        @Override
        public EventExecutorChooser newChooser(EventExecutor[] executors) {
            return new MahjongEventExecutorChooser(executors);
        }

        private static class MahjongEventExecutorChooser implements EventExecutorChooserFactory.EventExecutorChooser {

            private final AtomicInteger idx = new AtomicInteger();
            private final EventExecutor[] executors;

            MahjongEventExecutorChooser(EventExecutor[] executors) {
                this.executors = executors;
            }

            @Override
            public EventExecutor next() {
                Long roomId = MahjongContext.currentContext().getCurrentRoomId();
                long id;
                if (roomId != null) {
                    id = roomId;
                } else {
                    // 没获取到房间号的消息轮徇扔到业务线程池中处理
                    // 他们往往跟房间信息没啥关系，比如登录请求
                    id = idx.getAndIncrement();
                }
                return executors[(int) (id & executors.length - 1)];
            }
        }
    }
}
