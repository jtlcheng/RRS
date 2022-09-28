package com.cheng.rrs.order.controller.api;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cheng.rrs.common.result.Result;
import com.cheng.rrs.common.utils.AuthContextHolder;
import com.cheng.rrs.enums.OrderStatusEnum;
import com.cheng.rrs.model.order.OrderInfo;
import com.cheng.rrs.order.service.OrderInfoService;
import com.cheng.rrs.vo.order.OrderQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

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
    // 订单列表 条件查询带分页
    /**
    * @Description: 条件查询带分页
    * @Param: page limit
    * @return:
    * @Author: cheng
    * @Date: 2022/9/26 14:34
    */
    @ApiOperation("条件查询带分页")
    @GetMapping("auth/{page}/{limit}")
    public Result list(@PathVariable Long page,
                       @PathVariable Long limit,
                       OrderQueryVo orderQueryVo,
                       HttpServletRequest request){
        //设置当前用户id
        System.out.println("orderQueryVo.setUserId(AuthContextHolder.getUserId(request)):"+AuthContextHolder.getUserId(request));

        orderQueryVo.setUserId(AuthContextHolder.getUserId(request));
        Page<OrderInfo> pageParam=new Page<>(page,limit);
        IPage<OrderInfo> pageModel=orderInfoService.selectPage(pageParam,orderQueryVo);
        return Result.ok(pageModel);
    }

    //订单状态
    /**
    * @Description: 获取订单状态
    * @Param:
    * @return:
    * @Author: cheng
    * @Date: 2022/9/27 10:54
    */
    @ApiOperation("获取订单状态")
    @GetMapping("auth/getAuthStatusList")
    public Result getAuthStatusList(){
        return Result.ok(OrderStatusEnum.getStatusList());
    }

    //获取订单详情
    @ApiOperation("获取订单详情根据id")
    @GetMapping("auth/getOrders/{orderId}")
    public Result getOrders(@PathVariable String orderId){
        OrderInfo orderInfo=orderInfoService.getOrder(orderId);
        return Result.ok(orderInfo);
    }
}
