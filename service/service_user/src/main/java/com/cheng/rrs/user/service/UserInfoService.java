package com.cheng.rrs.user.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cheng.rrs.model.user.UserInfo;
import com.cheng.rrs.vo.user.LoginVo;
import com.cheng.rrs.vo.user.UserAuthVo;
import com.cheng.rrs.vo.user.UserInfoQueryVo;

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
    //用户认证接口
    void userAuth(Long userId, UserAuthVo userAuthVo);
    //用户列表(条件查询带分页)
    IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo);
    //锁定
    void lock(Long userId, Integer status);
    //用户详情
    Map<String, Object> show(Long userId);
    //认证审批
    void approval(Long userId, Integer authStatus);
}
