package com.cheng.rrs.hosp.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@MapperScan("com.cheng.rrs.hosp.mapper")
public class HosPConfig {
}
