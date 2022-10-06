package com.cheng.rrs.order.client;

import com.cheng.rrs.vo.order.OrderCountQueryVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.Map;

/**
 * @package: com.cheng.rrs.order.client
 * @Author: cheng
 * @Date: 2022-10-03 12:14
 **/
@FeignClient("service-order")
@Repository
public interface OrderFeignClient {

    /**
    * @Description: 获取预约统计数据方法
    * @Param:
    * @return:
    * @Author: cheng
    * @Date: 2022/10/3 12:16
    */
    @ApiOperation("获取预约统计数据方法")
    @PostMapping("/admin/order/orderInfo/inner/getCountMap")
    Map<String,Object> getCountMap(@RequestBody OrderCountQueryVo orderCountQueryVo);
}
