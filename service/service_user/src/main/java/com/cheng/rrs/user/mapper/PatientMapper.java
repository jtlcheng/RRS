package com.cheng.rrs.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.cheng.rrs.model.user.Patient;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;


/**
 * @package: com.cheng.rrs.user.mapper
 * @Author: cheng
 * @Date: 2022-09-19 09:23
 **/
@Mapper
@Repository
public interface PatientMapper extends BaseMapper<Patient> {
}
