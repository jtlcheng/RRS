package com.cheng.rrs.hosp.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cheng.rrs.hosp.mapper.HospitalSetMapper;
import com.cheng.rrs.hosp.service.HospitalService;
import com.cheng.rrs.model.hosp.HospitalSet;
import org.springframework.stereotype.Service;

@Service
public class HospitalServiceImpl extends ServiceImpl<HospitalSetMapper, HospitalSet> implements HospitalService {

}
