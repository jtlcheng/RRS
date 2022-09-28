package com.cheng.rrs.order.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cheng.rrs.common.result.Result;
import com.cheng.rrs.enums.OrderStatusEnum;
import com.cheng.rrs.model.order.OrderInfo;
import com.cheng.rrs.order.service.OrderInfoService;
import com.cheng.rrs.vo.order.OrderQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @package: com.cheng.rrs.order.controller
 * @Author: cheng
 * @Date: 2022-09-27 15:31
 **/
@RestController
@RequestMapping("/admin/order/orderInfo")
@Api(tags = "订单接口")
public class OrderController {
    @Autowired
    private OrderInfoService orderInfoService;

    //获取分页接口
    /**
    * @Description: page limit
    * @Param:
    * @return:
    * @Author: cheng
    * @Date: 2022/9/27 15:34
    */
    @ApiOperation("获取分页接口")
    @GetMapping("{page}/{limit}")
    public Result index(@PathVariable Long page,
                        @PathVariable Long limit,
                        OrderQueryVo orderQueryVo){
        Page<OrderInfo> pageParam=new Page<>(page,limit);

        IPage<OrderInfo> pageModel = orderInfoService.selectPage(pageParam, orderQueryVo);
        return Result.ok(pageModel);
    }
    //获取订单状态
    /**
    * @Description: 获取订单状态
    * @Param:
    * @return: OrderStatusEnum.getStatusList()
    * @Author: cheng
    * @Date: 2022/9/27 15:39
    */
    @ApiOperation("获取订单状态")
    @GetMapping("getAuthStatusList")
    public Result getAuthStatusList(){
        return Result.ok(OrderStatusEnum.getStatusList());
    }
    /**
    * @Description: 获取订单
    * @Param: id
    * @return:
    * @Author: cheng
    * @Date: 2022/9/27 16:44
    */
    //获取订单
    @ApiOperation("获取订单")
    @GetMapping("show/{id}")
    public Result getOrder(@PathVariable Long id){
        return Result.ok(orderInfoService.show(id));
    }
}
