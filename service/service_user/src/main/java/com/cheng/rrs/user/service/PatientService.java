package com.cheng.rrs.user.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cheng.rrs.model.user.Patient;

import java.util.List;

/**
 * @package: com.cheng.rrs.user.service
 * @Author: cheng
 * @Date: 2022-09-19 09:26
 **/
public interface PatientService extends IService<Patient> {
    //根据用户id获取就诊人列表
    List<Patient> findAllUserId(Long userId);
    //根据id获取就诊人信息
    Patient getPatientId(Long id);
}
