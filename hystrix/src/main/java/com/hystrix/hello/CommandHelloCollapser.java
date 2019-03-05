package com.hystrix.hello;

import com.netflix.hystrix.HystrixCollapser;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixCommandKey;
import com.netflix.hystrix.HystrixEventType;
import com.netflix.hystrix.HystrixInvokableInfo;
import com.netflix.hystrix.HystrixRequestLog;
import com.netflix.hystrix.strategy.concurrency.HystrixRequestContext;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

public class CommandHelloCollapser  extends HystrixCollapser<List<String>, String, Integer> {

    private final Integer key;

    public CommandHelloCollapser(Integer key) {
        this.key = key;
    }

    @Override
    public Integer getRequestArgument() {
        return key;
    }

    @Override
    protected HystrixCommand<List<String>> createCommand(final Collection<CollapsedRequest<String, Integer>> requests) {
        return new BatchCommand(requests);
    }

    @Override
    protected void mapResponseToRequests(List<String> batchResponse, Collection<CollapsedRequest<String, Integer>> requests) {
        int count = 0;
        for (CollapsedRequest<String, Integer> request : requests) {
            request.setResponse(batchResponse.get(count++));
        }
    }

    private static final class BatchCommand extends HystrixCommand<List<String>> {
        private final Collection<CollapsedRequest<String, Integer>> requests;

        private BatchCommand(Collection<CollapsedRequest<String, Integer>> requests) {
            super(Setter.withGroupKey(HystrixCommandGroupKey.Factory.asKey("ExampleGroup"))
                    .andCommandKey(HystrixCommandKey.Factory.asKey("Command Name:Hello Collapser")));
            this.requests = requests;
        }

        @Override
        protected List<String> run() {
            ArrayList<String> response = new ArrayList<String>();
            for (CollapsedRequest<String, Integer> request : requests) {
                // 在批处理中为每个接收的参数伪造响应
                response.add("ValueForKey: " + request.getArgument());
            }
            return response;
        }
    }

    public static class UnitTest {

        @Test
        public void testCollapser() throws Exception {
            HystrixRequestContext context = HystrixRequestContext.initializeContext();
            try {
                Future<String> f1 = new CommandHelloCollapser(1).queue();
                Future<String> f2 = new CommandHelloCollapser(2).queue();
                Future<String> f3 = new CommandHelloCollapser(3).queue();
                Future<String> f4 = new CommandHelloCollapser(4).queue();

                assertEquals("ValueForKey: 1", f1.get());
                assertEquals("ValueForKey: 2", f2.get());
                assertEquals("ValueForKey: 3", f3.get());
                assertEquals("ValueForKey: 4", f4.get());

                int numExecuted = HystrixRequestLog.getCurrentRequest().getAllExecutedCommands().size();

                System.err.println("num executed: " + numExecuted);

                // 断言此批次命令"Command Name:Hello Collapser"实际被执行了而却仅仅执行了它
                // 一次或两次(由于这个例子使用了真实的定时器，源于调度程序的不确定性)
                if (numExecuted > 2) {
                    fail("some of the commands should have been collapsed");
                }

                System.err.println("HystrixRequestLog.getCurrentRequest().getAllExecutedCommands(): " + HystrixRequestLog.getCurrentRequest().getAllExecutedCommands());

                int numLogs = 0;
                for (HystrixInvokableInfo<?> command : HystrixRequestLog.getCurrentRequest().getAllExecutedCommands()) {
                    numLogs++;

                    // 这个命令执行是我们所期望的
                    assertEquals("Command Name:Hello Collapser", command.getCommandKey().name());

                    System.err.println(command.getCommandKey().name() + " => command.getExecutionEvents(): " + command.getExecutionEvents());

                    // 确认是一个合并器的命令执行
                    assertTrue(command.getExecutionEvents().contains(HystrixEventType.COLLAPSED));
                    assertTrue(command.getExecutionEvents().contains(HystrixEventType.SUCCESS));
                }

                assertEquals(numExecuted, numLogs);
            } finally {
                context.shutdown();
            }
        }
    }
}
