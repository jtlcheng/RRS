package com.cheng.rrs.hosp.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cheng.rrs.hosp.mapper.HospitalSetMapper;
import com.cheng.rrs.hosp.service.HospitalSetService;
import com.cheng.rrs.model.hosp.HospitalSet;
import org.springframework.stereotype.Service;

@Service
public class HospitalServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalSetService {


    //2.根据传递过来的医院编码，查询数据库，查询签名
    @Override
    public String getSignKeys(String hoscode) {
        QueryWrapper <HospitalSet> wrapper=new QueryWrapper<>();
        wrapper.eq("hoscode",hoscode);
        HospitalSet hospitalSet = baseMapper.selectOne(wrapper);
        return hospitalSet.getSignKey();
    }
}
