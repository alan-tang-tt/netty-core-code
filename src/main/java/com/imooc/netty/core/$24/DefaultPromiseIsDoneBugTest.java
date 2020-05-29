package com.imooc.netty.core.$24;

import io.netty.util.concurrent.GlobalEventExecutor;
import io.netty.util.concurrent.Promise;

public class DefaultPromiseIsDoneBugTest {

    public static void main(String[] args) {
        Promise<?> defaultPromise = GlobalEventExecutor.INSTANCE.newPromise();
        defaultPromise.setUncancellable();
        boolean cancel = defaultPromise.cancel(false);
        boolean isDone = defaultPromise.isDone();
        System.out.println(cancel);
        System.out.println(isDone);
        System.out.println(defaultPromise.isCancelled());
    }

}
