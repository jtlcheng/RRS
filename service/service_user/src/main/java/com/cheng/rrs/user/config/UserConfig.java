package com.cheng.rrs.user.config;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.context.annotation.Configuration;

/**
 * @package: com.cheng.rrs.user.config
 * @Author: cheng
 * @Date: 2022-09-14 09:32
 **/
@Configuration
@MapperScan("com.cheng.rrs.user.mapper")
public class UserConfig {
}
