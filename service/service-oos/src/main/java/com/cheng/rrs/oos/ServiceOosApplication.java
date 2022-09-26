package com.cheng.rrs.oos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;

/**
 * @package: com.cheng.rrs.oos
 * @Author: cheng
 * @Date: 2022-09-18 14:13
 **/

@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)
@EnableDiscoveryClient
@ComponentScan(basePackages = "com.cheng")
public class ServiceOosApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceOosApplication.class,args);
    }
}
