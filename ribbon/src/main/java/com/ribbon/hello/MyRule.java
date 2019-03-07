package com.ribbon.hello;

import com.netflix.loadbalancer.ILoadBalancer;
import com.netflix.loadbalancer.IRule;
import com.netflix.loadbalancer.Server;

import java.util.List;
import java.util.Random;

/**
 * @author qll
 * @create 2019-03-06 16:19
 * @desc 自定义rule
 **/
public class MyRule implements IRule {
    private ILoadBalancer lb;
    @Override
    public Server choose(Object o) {
        //自定义的随机数大于7的就使用8081服务器，小于7就用8080服务器
        Random r = new Random();
        int rNum = r.nextInt(10);
        List<Server> servers = lb.getAllServers();
        if(rNum > 7){
            return getServerByPort(servers,8081);
        }
        return getServerByPort(servers,8080);
    }
    private Server getServerByPort(List<Server> servers ,int port){
        for (Server s : servers){
            if(s.getPort() == port){
                return s;
            }
        }
        return null;
    }

    @Override
    public void setLoadBalancer(ILoadBalancer iLoadBalancer) {
        this.lb = iLoadBalancer;
    }

    @Override
    public ILoadBalancer getLoadBalancer() {
        return this.lb;
    }
}
