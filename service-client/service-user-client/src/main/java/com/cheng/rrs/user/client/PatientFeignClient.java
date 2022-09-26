package com.cheng.rrs.user.client;

import com.cheng.rrs.model.user.Patient;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

/**
 * @package: com.cheng.rrs.user.client
 * @Author: cheng
 * @Date: 2022-09-23 11:13
 **/
@FeignClient(value = "service-user")
@Repository
public interface PatientFeignClient {
    //获取就诊人
    /**
    * @Description: 根据就诊人id获取就诊人信息
    * @Param: id
    * @return:
    * @Author: cheng
    * @Date: 2022/9/23 11:15
    */
    @ApiOperation("根据就诊人id获取就诊人信息")
    @GetMapping("/api/patient/auth/inner/get/{id}")
    Patient getPatientOrder(@PathVariable ("id") Long id);
}
