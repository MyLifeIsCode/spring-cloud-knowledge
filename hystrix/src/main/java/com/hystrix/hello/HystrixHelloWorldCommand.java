package com.hystrix.hello;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixThreadPoolKey;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
public class HystrixHelloWorldCommand extends HystrixCommand<String> {
    private final String name;

    private static final Setter cacheSetter = Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("Command Group: Hello World"))
            .andCommandKey(HystrixCommandKey.Factory.asKey("Command Name: Hello World"))
            .andThreadPoolKey(HystrixThreadPoolKey.Factory.asKey("Command Thread: Hello World"));
    public HystrixHelloWorldCommand(String name) {
        super(cacheSetter);
        this.name = name;
    }

    @Override
    protected String run() throws Exception {
        return "Hello " + name + "!";
    }
    public static class UnitTest {

        @Test
        public void testCommandName(){
            assertEquals("Command Group: Hello World",new HystrixHelloWorldCommand("World").getCommandGroup().name());
            assertEquals("Command Name: Hello World", new HystrixHelloWorldCommand("World").getCommandKey().name());
            assertEquals("Command ThreadPool: Hello World", new HystrixHelloWorldCommand("World").getThreadPoolKey().name());
        }
    }
}


















