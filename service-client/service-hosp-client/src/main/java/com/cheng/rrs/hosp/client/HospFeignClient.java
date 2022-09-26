package com.cheng.rrs.hosp.client;

import com.cheng.rrs.vo.hosp.ScheduleOrderVo;
import com.cheng.rrs.vo.order.SignInfoVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("service-hosp")
@Repository
public interface HospFeignClient {

    //根据排班id获取预约下单数据
    /**
    * @Description: 根据排班id获取预约下单数据
    * @Param: scheduleId
    * @return:
    * @Author: cheng
    * @Date: 2022/9/23 13:01
    */
    @GetMapping("/api/hosp/hospital/inner/getScheduleOrderVo/{scheduleId}")
    @ApiOperation("根据排班id获取预约下单数据")
    ScheduleOrderVo getScheduleOrderVo(@PathVariable String scheduleId);

    //获取医院签名信息
    /**
    * @Description: hoscode
    * @Param: 获取医院签名信息
    * @return:
    * @Author: cheng
    * @Date: 2022/9/23 13:02
    */
    @ApiOperation("获取医院签名信息")
    @GetMapping("/api/hosp/hospital/inner/getSignInfoVo/{hoscode}")
    SignInfoVo getSignInfoVo(@PathVariable String hoscode);

}
