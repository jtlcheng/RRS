package com.cheng.rrs.task;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

import javax.sql.DataSource;

/**
 * @package: com.cheng.rrs.task
 * @Author: cheng
 * @Date: 2022-10-03 09:40
 **/
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//取消数据源自动配置
@ComponentScan(basePackages = "com.cheng")
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.cheng")
public class ServiceTaskApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceTaskApplication.class,args);
    }
}
