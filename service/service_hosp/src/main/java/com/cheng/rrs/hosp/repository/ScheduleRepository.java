package com.cheng.rrs.hosp.repository;

import com.cheng.rrs.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ScheduleRepository extends MongoRepository<Schedule,String> {

    //根据医院编号和排班编号查询
    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);
}
