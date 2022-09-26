package com.cheng.rrs.msm.service;

import com.cheng.rrs.vo.msm.MsmVo;

/**
 * @package: com.cheng.rrs.msm.service
 * @Author: cheng
 * @Date: 2022-09-14 16:40
 **/
public interface MsmService{
    //发送短信验证码
    boolean send(String phone, String code);

    //mq使用发送短信
    boolean send(MsmVo msmVo);
}
