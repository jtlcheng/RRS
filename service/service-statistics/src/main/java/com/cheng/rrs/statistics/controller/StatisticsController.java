package com.cheng.rrs.statistics.controller;

import com.cheng.rrs.common.result.Result;
import com.cheng.rrs.order.client.OrderFeignClient;
import com.cheng.rrs.vo.order.OrderCountQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @package: com.cheng.rrs.statistics.controller
 * @Author: cheng
 * @Date: 2022-10-03 12:26
 **/
@RestController
@Api(tags = "统计管理接口")
@RequestMapping("/admin/statistics")
public class StatisticsController {
    @Autowired
    private OrderFeignClient orderFeignClient;

    //获取预约统计数据
    /**
    * @Description: 获取预约统计数据
    * @Param:
    * @return:
    * @Author: cheng
    * @Date: 2022/10/3 12:28
    */
    @ApiOperation("获取预约统计数据")
    @GetMapping("getCountMap")
    public Result getCountMap(OrderCountQueryVo orderCountQueryVo){
        Map<String, Object> countMap = orderFeignClient.getCountMap(orderCountQueryVo);
        return Result.ok(countMap);
    }
}
