package com.cheng.rrs.common.utils;

import com.cheng.rrs.common.helper.JwtHelper;

import javax.servlet.http.HttpServletRequest;

/**
 * @package: com.cheng.rrs.common.utils
 * @Author: cheng
 * @Date: 2022-09-18 16:03
 **/
//获取当前用户信息工具类
public class AuthContextHolder {
    //获取当前用户id
    public static Long getUserId(HttpServletRequest request){
        //从header获取token
        String token = request.getHeader("token");
        System.out.println("token:"+token);
        //jwt从token获取userid
        Long userId = JwtHelper.getUserId(token);
        System.out.println("userId"+userId);

        return userId;
    }
    //获取当前用户名称
    public static String getUserName(HttpServletRequest request){
        //从header获取token
        String token = request.getHeader("token");
        //jwt从token获取userName
        String userName = JwtHelper.getUserName(token);
        return userName;
    }
}

