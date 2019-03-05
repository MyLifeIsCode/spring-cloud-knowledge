package com.hystrix.hello;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;

import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

public class HystrixHelloWorldFallback extends HystrixCommand<String> {

    private final String name;
    private static final Setter cacheSetter = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("command group: hello world")
        ).andCommandKey(HystrixCommandKey.Factory.asKey("command name: hello world"))
        .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("Command ThreadPool: Hello World"));
    public HystrixHelloWorldFallback(String name){
        super(cacheSetter);
        this.name = name;
    }


    @Override
    protected String run() throws Exception {
        throw new RuntimeException("模拟异常");
    }

    @Override
    public String getFallback(){
        return "Hello Failure " + name + "!";
    }
    public static class UnitTest{
        @Test
        public void testSynchronous() {
            assertEquals("Hello Failure World!", new HystrixHelloWorldFallback("World").execute());
            assertEquals("Hello Failure Bob!", new HystrixHelloWorldFallback("Bob").execute());
        }

    }

}










































