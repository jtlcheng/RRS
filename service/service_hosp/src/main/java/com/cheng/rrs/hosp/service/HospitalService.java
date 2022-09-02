package com.cheng.rrs.hosp.service;

import com.cheng.rrs.model.hosp.Hospital;

import java.util.Map;

public interface HospitalService {
    //上传医院接口
    void save(Map<String, Object> map);
    //根据医院编号查询
    Hospital getByHoscode(String hoscode);
}
