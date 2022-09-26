package com.cheng.rrs.hosp.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.cheng.rrs.model.hosp.Schedule;
import com.cheng.rrs.vo.hosp.ScheduleOrderVo;
import com.cheng.rrs.vo.hosp.ScheduleQueryVo;
import org.apache.ibatis.annotations.Mapper;
import org.springframework.stereotype.Repository;

/**
 * @package: com.cheng.rrs.hosp.mapper
 * @Author: cheng
 * @Date: 2022-09-23 11:29
 **/
@Mapper
@Repository
public interface ScheduleMapper extends BaseMapper<Schedule> {
}
