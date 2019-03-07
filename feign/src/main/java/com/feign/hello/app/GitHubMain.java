package com.feign.hello.app;

import com.feign.hello.interfaces.GitHub;
import feign.Feign;
import feign.gson.GsonDecoder;

import java.util.List;

/**
 * @author qll
 * @create 2019-03-06 11:20
 * @desc 调用github
 **/
public class GitHubMain {
    public static void main(String[] args) {
        GitHub gitHub = Feign.builder().decoder(new GsonDecoder())
                .target(GitHub.class,"https://api.github.com");

        gitHub.contributors("OpenFeign", "feign");
        List<GitHub.Contributor> contributors = gitHub.contributors("OpenFeign", "feign");
        for (GitHub.Contributor contributor : contributors) {
            System.out.println(contributor.getLogin() + " (" + contributor.getContributions() + ")");
        }
    }
}
