package com.cheng.rrs.hosp.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cheng.rrs.model.hosp.Schedule;
import com.cheng.rrs.vo.hosp.ScheduleOrderVo;
import com.cheng.rrs.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService extends IService<Schedule> {
    //上传排班接口
    void save(Map<String, Object> paramMap);

    //查询排班接口 分页
    Page<Schedule> findPageSchedule(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo);

    //删除排班接口
    void remove(String hoscode, String hosScheduleId);

    //排班详情
    Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode);

    //根据医院编号、科室编号、工作日期查询排班信息
    List<Schedule> getDetailSchedule(String hoscode, String depcode, String workDate);

    //获取可预约排班数据
    Map<String, Object> getBookingRuleSchedule(Integer page, Integer limit, String hoscode, String depcode);

    //根据排班id获取排班数据
    Schedule getById(String scheduleId);

    //根据排班id获取预约下单数据
    ScheduleOrderVo getScheduleOrderVo(String scheduleId);

    //更新排班数据 用于mq
    void update(Schedule schedule);
}
