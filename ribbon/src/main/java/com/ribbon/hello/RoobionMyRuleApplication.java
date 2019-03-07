package com.ribbon.hello;

import com.netflix.loadbalancer.BaseLoadBalancer;
import com.netflix.loadbalancer.Server;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.ArrayList;
import java.util.List;

/**
 * @author qll
 * @create 2019-03-06 16:25
 * @desc myrule
 **/
@SpringBootApplication
public class RoobionMyRuleApplication {
    public static void main(String[] args) {
        SpringApplication.run(RoobionMyRuleApplication.class,args);
        BaseLoadBalancer lb = new BaseLoadBalancer();
        MyRule rule = new MyRule();
        rule.setLoadBalancer(lb);
        lb.setRule(rule);
        List<Server> servers = new ArrayList<Server>();
        servers.add(new Server("localhost", 8080));
        servers.add(new Server("localhost", 8081));
        lb.addServers(servers);
        for (int i = 0; i < 10; i++) {
            Server s = lb.chooseServer(null);
            System.out.println(s);
        }

    }
}
