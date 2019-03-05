package com.hystrix.hello;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixRequestCache;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;

import java.util.concurrent.Future;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class CommandHelloRequestCache extends HystrixCommand<Boolean> {
    private final int value;

    protected CommandHelloRequestCache(int value) {
        super(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"));
        this.value = value;
    }
    @Override
    protected Boolean run() throws Exception {
        return value == 0 || value % 2 == 0;
    }

    @Override
    protected String getCacheKey() {
        return String.valueOf(value);
    }

    public static class UnitTest {
        @Test
        public void testWithCacheHits() {
            HystrixRequestContext context = HystrixRequestContext.initializeContext();
            try {
                CommandHelloRequestCache command2a = new CommandHelloRequestCache(2);
                CommandHelloRequestCache command2b = new CommandHelloRequestCache(2);

                assertTrue(command2a.execute());
                // 第一次执行这个命令时不是从缓存返回的值
                assertFalse(command2a.isResponseFromCache());

                assertTrue(command2b.execute());
                // 第二次执行这个命令时是从缓存返回的值
                assertTrue(command2b.isResponseFromCache());
            } finally {
                context.shutdown();
            }

            // 开启一个新的请求上下文
            context = HystrixRequestContext.initializeContext();
            try {
                CommandHelloRequestCache command3b = new CommandHelloRequestCache(2);
                assertTrue(command3b.execute());
                // 这个新请求执行这个命令也不会从缓存返回值
                assertFalse(command3b.isResponseFromCache());
            } finally {
                context.shutdown();
            }
        }
    }

}
































