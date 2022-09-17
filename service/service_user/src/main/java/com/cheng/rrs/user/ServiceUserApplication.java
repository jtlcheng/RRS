package com.cheng.rrs.user;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @package: com.cheng.rrs.user
 * @Author: cheng
 * @Date: 2022-09-14 09:29
 **/
@SpringBootApplication
@ComponentScan(basePackages = "com.cheng")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.cheng")
public class ServiceUserApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceUserApplication.class,args);
    }
}
