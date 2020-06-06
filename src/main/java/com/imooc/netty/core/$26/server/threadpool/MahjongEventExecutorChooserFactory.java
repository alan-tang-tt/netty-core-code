package com.imooc.netty.core.$26.server.threadpool;

import io.netty.util.concurrent.EventExecutor;
import io.netty.util.concurrent.EventExecutorChooserFactory;
import io.netty.util.concurrent.FastThreadLocal;

public class MahjongEventExecutorChooserFactory implements EventExecutorChooserFactory {
    public static final MahjongEventExecutorChooserFactory INSTANCE = new MahjongEventExecutorChooserFactory();

    private static final FastThreadLocal<Long> TABLE_ID_THREAD_LOCAL = new FastThreadLocal<>();

    @Override
    public EventExecutorChooser newChooser(EventExecutor[] executors) {
        return new MahjongEventExecutorChooser(executors);
    }

    public static void setTableId(Long tableId) {
        TABLE_ID_THREAD_LOCAL.set(tableId);
    }

    private static class MahjongEventExecutorChooser implements EventExecutorChooserFactory.EventExecutorChooser {

        private final EventExecutor[] executors;

        MahjongEventExecutorChooser(EventExecutor[] executors) {
            this.executors = executors;
        }

        @Override
        public EventExecutor next() {
            try {
                // 按桌子id定位到同一个线程
                Long tableId = TABLE_ID_THREAD_LOCAL.get();
                if (tableId == null) {
                    tableId = 0L;
                }
                return executors[(int) (tableId % executors.length)];
            } finally {
                TABLE_ID_THREAD_LOCAL.remove();
            }
        }
    }
}
