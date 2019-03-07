package com.feign.hello.interfaces;


import feign.Param;
import feign.RequestLine;

import java.util.List;

public interface GitHub {

    // RequestLine注解声明请求方法和请求地址,可以允许有查询参数
    @RequestLine("GET /repos/{owner}/{repo}/contributors")
    List<Contributor> contributors(@Param("owner") String owner,@Param("repo") String repo);

    static class Contributor{
        String login;
        int contributions;

        public String getLogin() {
            return login;
        }

        public void setLogin(String login) {
            this.login = login;
        }

        public int getContributions() {
            return contributions;
        }

        public void setContributions(int contributions) {
            this.contributions = contributions;
        }
    }
}
