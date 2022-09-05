package com.cheng.rrs.hosp.repository;

import com.cheng.rrs.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * mongodb 排班管理
 * @author cheng
 * @version 1.0
 */
public interface ScheduleRepository extends MongoRepository<Schedule,String> {

    //根据医院编号和排班编号查询
    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);
}
