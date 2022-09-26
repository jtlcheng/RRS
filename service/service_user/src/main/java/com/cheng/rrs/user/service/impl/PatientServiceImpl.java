package com.cheng.rrs.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cheng.rrs.cmn.client.DictFeignClient;
import com.cheng.rrs.enums.DictEnum;
import com.cheng.rrs.model.user.Patient;
import com.cheng.rrs.model.user.UserInfo;
import com.cheng.rrs.user.mapper.PatientMapper;
import com.cheng.rrs.user.mapper.UserInfoMapper;
import com.cheng.rrs.user.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * @package: com.cheng.rrs.user.service.impl
 * @Author: cheng
 * @Date: 2022-09-19 09:27
 **/
@Service
public class PatientServiceImpl extends ServiceImpl<PatientMapper, Patient> implements PatientService {

    @Autowired
    private DictFeignClient dictFeignClient;

    //根据用户id获取就诊人列表
    @Override

    public List<Patient> findAllUserId(Long userId) {
        //根据userId查询所有就诊人信息列表
        QueryWrapper<Patient> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id", userId);
        List<Patient> patientList = baseMapper.selectList(queryWrapper);
        //通过远程调用，得到编码对应具体内容，查询数据字典表内容
        patientList.stream().forEach(item -> {
            //其他参数封装
            this.packPatient(item);
        });

        return patientList;
    }

    //根据id获取就诊人信息
    @Override

    public Patient getPatientId(Long id) {
        return this.packPatient(baseMapper.selectById(id));
    }

    //Patient对象里面其他参数封装
    private Patient packPatient(Patient patient) {
        //根据证件类型编码，获取证件类型具体
        String certificatesTypeString =
                dictFeignClient.getName
                        (DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getCertificatesType());//联系人证件
        //联系人证件类型
        String contactsCertificatesTypeString =
                dictFeignClient.getName
                        (DictEnum.CERTIFICATES_TYPE.getDictCode(), patient.getContactsCertificatesType());
        //省
        String provinceString =
                dictFeignClient.getName
                        (patient.getProvinceCode());
        //市
        String cityString =
                dictFeignClient.getName
                        (patient.getCityCode());
        //区
        String districtString =
                dictFeignClient.getName
                        (patient.getDistrictCode());
        patient.getParam().
                put("certificatesTypeString", certificatesTypeString);
        patient.getParam().
                put("contactsCertificatesTypeString", contactsCertificatesTypeString);
        patient.getParam().
                put("provinceString", provinceString);
        patient.getParam().
                put("cityString", cityString);
        patient.getParam().
                put("districtString", districtString);
        patient.getParam().
                put("fullAddress", provinceString + cityString + districtString);

        return patient;

    }
}
