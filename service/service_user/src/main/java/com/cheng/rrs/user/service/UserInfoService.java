package com.cheng.rrs.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cheng.rrs.model.user.UserInfo;
import com.cheng.rrs.vo.user.LoginVo;

import java.util.Map;

/**
 * @package: com.cheng.rrs.user.service
 * @Author: cheng
 * @Date: 2022-09-14 09:34
 **/
public interface UserInfoService extends IService<UserInfo> {
    //用户手机号登录
    Map<String,Object> loginUser(LoginVo loginVo);
    //openid判断
    UserInfo selectWxInfoOpenId(String openid);
}
