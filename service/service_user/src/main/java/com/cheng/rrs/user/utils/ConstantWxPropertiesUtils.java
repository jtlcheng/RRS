package com.cheng.rrs.user.utils;


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
public class ConstantWxPropertiesUtils implements InitializingBean{

    @Value("${wx.open.app_id}")
    private String appId;

    @Value("${wx.open.app_secret}")
    private String appSecret;

    @Value("${wx.open.redirect_url}")
    private String redirectUrl;

    @Value("${yygh.baseUrl}")
    private String yyghBaseUrl;


    public static String WX_OPEN_APP_ID;
    public static String WX_OPEN_APP_SECRET;
    public static String WX_OPEN_REDIRECT_URL;

    public static String YYGH_BASE_URL;


    public ConstantWxPropertiesUtils() {
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        WX_OPEN_APP_ID = appId;
        WX_OPEN_APP_SECRET = appSecret;
        WX_OPEN_REDIRECT_URL = redirectUrl;
        YYGH_BASE_URL = yyghBaseUrl;
    }
}
