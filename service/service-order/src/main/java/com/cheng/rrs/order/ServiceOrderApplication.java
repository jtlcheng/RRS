package com.cheng.rrs.order;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @package: com.cheng.rrs.order
 * @Author: cheng
 * @Date: 2022-09-23 10:48
 **/
@SpringBootApplication
@ComponentScan(basePackages = "com.cheng")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.cheng")
public class ServiceOrderApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceOrderApplication.class,args);
    }
}
