package com.ribbon.hello;

import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.RoundRobinRule;
import com.netflix.loadbalancer.Server;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qll
 * @create 2019-03-06 16:10
 * @desc first test
 **/
@SpringBootApplication
public class RibbonUserApplication {
    public static void main(String[] args) {

        BaseLoadBalancer lb = new BaseLoadBalancer();
        List<Server> servers = new ArrayList<Server>(){{
            add(new Server("localhost",8080));
            add(new Server("localhost",8081));
        }};

        lb.addServers(servers);
        for (int i=0 ;i<10 ;i++){
            Server server = lb.chooseServer();
            System.out.println(server);
        }
    }
}
