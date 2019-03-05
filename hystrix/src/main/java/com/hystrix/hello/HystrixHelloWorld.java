package com.hystrix.hello;

import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import org.junit.Test;
import rx.Observable;
import rx.Observer;
import rx.functions.Action1;

import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;

/**
 * @author qll
 * @create 2019-03-05 20:38
 * @desc hello world
 **/
public class HystrixHelloWorld extends HystrixCommand<String> {
    private final String name;
    public HystrixHelloWorld(String name){
        super(HystrixCommandGroupKey.Factory.asKey("exampleGroup"));
        this.name = name;
    }
    @Override
    protected String run() throws Exception {
        return "Hello " + name + "!";
    }

    public static class UnitTest{


        @Test
        public void testSynchronous() { //测试同步
            //这是应用非常广泛的一个断言，它的作用是比较实际的值和用户预期的值是否一样，
            // assertEquals在JUnit中有很多不同的实现
            assertEquals("Hello World!", new HystrixHelloWorld("World").execute());
            assertEquals("Hello Bob!", new HystrixHelloWorld("Bob").execute());
        }
        @Test
        public void testAsynchronous2() throws Exception{//测试异步
            Future<String> fWorld = new HystrixHelloWorld("World").queue();
            Future<String> fBob = new HystrixHelloWorld("Bob").queue();
            assertEquals("Hello World!",fWorld.get());
            assertEquals("Hello Bob!",fBob.get());
        }

        @Test
        public void testObservable() throws Exception {

            Observable<String> fWorld = new HystrixHelloWorld("World").observe();
            Observable<String> fBob = new HystrixHelloWorld("Bob").observe();

            // blocking
            assertEquals("Hello World!", fWorld.toBlocking().single());
            assertEquals("Hello Bob!", fBob.toBlocking().single());

            fWorld.subscribe(new Observer<String>() {

                @Override
                public void onCompleted() {
                    // 这里可以什么都不做
                }

                @Override
                public void onError(Throwable e) {
                    e.printStackTrace();
                }

                @Override
                public void onNext(String v) {
                    System.out.println("onNext: " + v);
                }

            });
            fBob.subscribe(new Action1<String>() {

                @Override
                public void call(String v) {
                    System.out.println("onNext: " + v);
                }

            });
        }
    }

}
































