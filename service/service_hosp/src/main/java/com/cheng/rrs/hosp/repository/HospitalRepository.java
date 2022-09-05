package com.cheng.rrs.hosp.repository;

import com.cheng.rrs.model.hosp.Hospital;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * mongodb 医院设置与管理
 * @author cheng
 * @version 1.0
 *
 */
@Repository
public interface HospitalRepository extends MongoRepository<Hospital,String> {
    //判断是否存在数据
    Hospital getHospitalByHoscode(String hoscode);
}
