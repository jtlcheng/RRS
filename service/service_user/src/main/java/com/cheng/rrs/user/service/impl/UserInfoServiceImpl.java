package com.cheng.rrs.user.service.impl;

import com.alibaba.excel.util.StringUtils;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cheng.rrs.common.exception.YyghException;
import com.cheng.rrs.common.helper.JwtHelper;
import com.cheng.rrs.common.result.ResultCodeEnum;
import com.cheng.rrs.enums.AuthStatusEnum;
import com.cheng.rrs.model.user.Patient;
import com.cheng.rrs.model.user.UserInfo;
import com.cheng.rrs.user.mapper.UserInfoMapper;
import com.cheng.rrs.user.service.PatientService;
import com.cheng.rrs.user.service.UserInfoService;
import com.cheng.rrs.vo.user.LoginVo;
import com.cheng.rrs.vo.user.UserAuthVo;
import com.cheng.rrs.vo.user.UserInfoQueryVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @package: com.cheng.rrs.user.service.impl
 * @Author: cheng
 * @Date: 2022-09-14 09:35
 **/
@Service
public class UserInfoServiceImpl extends ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private PatientService patientService;

    @Override
    public Map<String, Object> loginUser(LoginVo loginVo) {
        //从loginVo获取输入的手机号和验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        //校验参数 判断手机号和验证码是否为空
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //TODO 判断手机号验证码和输入的验证码是否一致
        String mobleCode = redisTemplate.opsForValue().get(phone);
        if (!code.equals(mobleCode)) {
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }
        //绑定手机号码
        UserInfo userInfo = new UserInfo();
        if (!StringUtils.isEmpty(loginVo.getOpenid())) {
            userInfo = this.selectWxInfoOpenId(loginVo.getOpenid());
            if (null != userInfo) {
                userInfo.setPhone(loginVo.getPhone());
                this.updateById(userInfo);

            } else {
                throw new YyghException(ResultCodeEnum.DATA_ERROR);
            }
            System.out.println("绑定手机号码");
        }
        //如果userInfo为空 进行正常手机登录
        if (userInfo == null) {
            //判断是否第一次登录 根据手机号查询数据库 如果不存在相同手机号就是第一次登录
            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("phone", phone);
            userInfo = baseMapper.selectOne(wrapper);

            if (userInfo == null) {//第一次使用这个手机号登录
                //添加信息到数据库
                userInfo = new UserInfo();
                userInfo.setName("");
                userInfo.setPhone(phone);
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
                System.out.println("不绑定手机号码");
            }
            System.out.println("不绑定手机号码");

        }
        //校验是否被禁用
        if (userInfo.getStatus() == 0) {
            throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }
        //不是第一次登录 直接登录
        //返回登录用户名
        //返回token信息
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);
        //jwt 生成token字符串
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);
        return map;
    }

    //openid判断
    @Override
    public UserInfo selectWxInfoOpenId(String openid) {
        QueryWrapper<UserInfo> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("openid", openid);
        System.out.println("UserInfo"+openid);
        UserInfo userInfo = baseMapper.selectOne(queryWrapper);
        System.out.println(userInfo);
        return userInfo;
    }

    //用户认证接口
    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        //根据用户id查询用户信息
        UserInfo userInfo = baseMapper.selectById(userId);
        //设置认证信息
        //认证人信息
        userInfo.setName(userAuthVo.getName());
        //其他认证信息
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setCertificatesUrl(userAuthVo.getCertificatesUrl());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());

        //进行信息更新
        baseMapper.updateById(userInfo);
    }

    //用户列表(条件查询带分页)
    @Override
    public IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo) {
        //userInfoQueryVo获取条件值
        String name = userInfoQueryVo.getKeyword();//用户名称
        Integer status = userInfoQueryVo.getStatus();//用户状态
        Integer authStatus = userInfoQueryVo.getAuthStatus(); //认证状态
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin(); //开始时间
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd(); //结束时间
        //对条件值进行非空判断
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        if(!StringUtils.isEmpty(name)) {
            wrapper.like("name",name);
        }
        if(!StringUtils.isEmpty(status)) {
            wrapper.eq("status",status);
        }
        if(!StringUtils.isEmpty(authStatus)) {
            wrapper.eq("auth_status",authStatus);
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time",createTimeEnd);
        }
        //调用mapper的方法
        IPage<UserInfo> pages = baseMapper.selectPage(pageParam, wrapper);
        //编号变成对应值封装
        pages.getRecords().stream().forEach(item -> {
            this.packageUserInfo(item);
        });
        return pages;
    }
    //锁定
    @Override
    public void lock(Long userId, Integer status) {
        if (status.intValue()==0||status.intValue()==1){
            UserInfo userInfo = this.getById(userId);
            userInfo.setStatus(status);
            this.updateById(userInfo);
        }
    }
    //用户详情
    @Override
    public Map<String, Object> show(Long userId) {
        Map<String,Object> map=new HashMap<>();
        //根据UserId 查询用户信息
        UserInfo userInfo = this.packageUserInfo(baseMapper.selectById(userId));
        map.put("userInfo",userInfo);
        //根据userId查询就诊人信息
        List<Patient> patientList = patientService.findAllUserId(userId);
        map.put("patientList",patientList);
        return map;
    }
    //认证审批
    @Override
    public void approval(Long userId, Integer authStatus) {
        if (authStatus.intValue()==2||authStatus.intValue()==-1){
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }
    }

    //编号变成对应值封装
    private UserInfo packageUserInfo(UserInfo userInfo) {
        //处理认证状态编码
        userInfo.getParam().put("authStatusString",AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));
        //处理用户状态 0  1
        String statusString = userInfo.getStatus().intValue()==0 ?"锁定" : "正常";
        userInfo.getParam().put("statusString",statusString);
        return userInfo;
    }
}
