package com.cheng.rrs.hosp.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.cheng.rrs.hosp.repository.HospitalRepository;
import com.cheng.rrs.hosp.service.HospitalService;
import com.cheng.rrs.model.hosp.Hospital;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;


@Service
public class HospitalSerServiceImpl implements HospitalService {

    @Autowired
    private HospitalRepository hospitalRepository;
    @Override
    public void save(Map<String, Object> map) {
        //把参数map集合转换对象 Hospital
        String s = JSONObject.toJSONString(map);
        Hospital hospital = JSONObject.parseObject(s, Hospital.class);

        //判断是否存在数据
        String hoscode = hospital.getHoscode();
        Hospital hospitalExist=hospitalRepository.getHospitalByHoscode(hoscode);

        //如果存在进行修改
        if (hospitalExist!=null){
            hospitalExist.setId(hospital.getId());
            hospital.setStatus(hospitalExist.getStatus());
            hospital.setCreateTime(hospitalExist.getCreateTime());
            hospital.setUpdateTime(hospitalExist.getUpdateTime());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospitalExist);
        }else { //如果不存在进行添加
            hospital.setStatus(0);
            hospital.setCreateTime(new Date());
            hospital.setUpdateTime(new Date());
            hospital.setIsDeleted(0);
            hospitalRepository.save(hospital);
        }
    }

    //根据医院编号查询
    @Override
    public Hospital getByHoscode(String hoscode) {
        Hospital hospital = hospitalRepository.getHospitalByHoscode(hoscode);
        return hospital;
    }
}
