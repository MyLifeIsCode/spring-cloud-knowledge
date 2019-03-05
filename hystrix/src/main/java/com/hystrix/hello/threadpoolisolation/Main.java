package com.hystrix.hello.threadpoolisolation;

import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static com.sun.xml.internal.ws.spi.db.BindingContextFactory.LOGGER;

public class Main {
    public static void main(String[] args) throws Exception {
        CommandOrder commandPhone = new CommandOrder("手机");
        CommandOrder command = new CommandOrder("电视");


        //阻塞方式执行
        String execute = commandPhone.execute();
        LOGGER.info(String.format("execute=[%s]", execute));

        //异步非阻塞方式
        Future<String> queue = command.queue();
        String value = queue.get(200, TimeUnit.MILLISECONDS);
        LOGGER.info(String.format("value=[%s]", value));


        CommandUser commandUser = new CommandUser("张三");
        String name = commandUser.execute();
        LOGGER.info(String.format("name=[%s]", name));
    }
}
