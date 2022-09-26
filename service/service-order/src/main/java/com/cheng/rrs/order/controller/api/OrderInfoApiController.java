package com.cheng.rrs.order.controller.api;

import com.cheng.rrs.common.result.Result;
import com.cheng.rrs.order.service.OrderInfoService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @package: com.cheng.rrs.order.controller
 * @Author: cheng
 * @Date: 2022-09-23 10:57
 **/
@RestController
@RequestMapping("/api/order/orderInfo")
@Api(tags = "订单接口")
public class OrderInfoApiController {
    @Autowired
    private OrderInfoService orderInfoService;

    //创建订单
    /**
    * @Description: 创建订单
    * @Param: scheduleId patientId
    * @return:
    * @Author: cheng
    * @Date: 2022/9/23 11:00
    */
    @ApiOperation("创建订单")
    @PostMapping("auth/submitOrder/{scheduleId}/{patientId}")
    public Result submitOrder(@PathVariable String scheduleId,
                              @PathVariable Long patientId){
        return Result.ok(orderInfoService.saveOrder(scheduleId,patientId));
    }

}
