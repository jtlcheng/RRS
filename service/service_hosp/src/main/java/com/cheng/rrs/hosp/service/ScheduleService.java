package com.cheng.rrs.hosp.service;

import com.cheng.rrs.model.hosp.Schedule;
import com.cheng.rrs.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.Map;

public interface ScheduleService {
    //上传排班接口
    void save(Map<String, Object> paramMap);

    //查询排班接口 分页
    Page<Schedule> findPageSchedule(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo);

    //删除排班接口
    void remove(String hoscode, String hosScheduleId);
}
