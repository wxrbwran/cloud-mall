package com.imooc.cloud.mall.practice.zuul;

import org.springframework.boot.SpringApplication;
import org.springframework.cloud.client.SpringCloudApplication;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.session.data.redis.config.annotation.web.http.EnableRedisHttpSession;

@EnableZuulProxy
@EnableFeignClients
@EnableRedisHttpSession
@SpringCloudApplication
public class ZuulGatewayApplication {
  public static void main(String[] args) {
    SpringApplication.run(ZuulGatewayApplication.class, args);
  }
}
