package com.cheng.rrs.statistics;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * @package: com.cheng.rrs.statistics
 * @Author: cheng
 * @Date: 2022-10-03 12:21
 **/
@SpringBootApplication(exclude = DataSourceAutoConfiguration.class)//取消数据源自动配置
@EnableDiscoveryClient
@EnableFeignClients(basePackages = "com.cheng")
@ComponentScan(basePackages = "com.cheng")
public class ServiceStatisticsApplication {
    public static void main(String[] args) {
        SpringApplication.run(ServiceStatisticsApplication.class,args);
    }

}
