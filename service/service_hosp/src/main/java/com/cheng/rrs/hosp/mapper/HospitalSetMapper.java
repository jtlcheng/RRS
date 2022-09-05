package com.cheng.rrs.hosp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cheng.rrs.model.hosp.HospitalSet;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * mybatisPlus配置
 * @author cheng
 * @version 1.0
 */
@Mapper
@Repository
public interface HospitalSetMapper extends BaseMapper<HospitalSet> {

}
