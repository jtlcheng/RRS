package com.cheng.rrs.oos.utils;


import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

/**
 * @package: com.cheng.rrs.msm.utils
 * @Author: cheng
 * @Date: 2022-09-14 16:07
 **/
@Component
@Repository
public class ConstantOssPropertiesUtils implements InitializingBean{

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Value("${aliyun.oss.accessKeyId}")
    private String accessKeyId;

    @Value("${aliyun.oss.secret}")
    private String secret;
    @Value("${aliyun.oss.bucket}")
    private String bucket;

    public ConstantOssPropertiesUtils() {
    }
    public static String ENDPOINT_URL;
    public static String ACCESS_KEY_ID;
    public static String SECRET;
    public static String BUCKET;

    @Override
    public void afterPropertiesSet() throws Exception {
        ENDPOINT_URL=endpoint;
        ACCESS_KEY_ID=accessKeyId;
        SECRET=secret;
        BUCKET=bucket;

    }
}
