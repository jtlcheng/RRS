package com.cheng.rrs.order.controller.api;

import com.cheng.rrs.common.result.Result;
import com.cheng.rrs.order.service.PaymentService;
import com.cheng.rrs.order.service.WeixinService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * @package: com.cheng.rrs.order.controller.api
 * @Author: cheng
 * @Date: 2022-09-30 09:55
 **/
@RestController
@RequestMapping("/api/order/weixin")
@Api(tags = "微信支付")
public class WeixinController {
    @Autowired
    private WeixinService weixinService;
    @Autowired
    private PaymentService paymentService;
    //生成微信支付二维码
    /**
    * @Description: 生成微信支付二维码
    * @Param: orderId
    * @return:
    * @Author: cheng
    * @Date: 2022/9/30 9:59
    */
    @ApiOperation("生成微信支付二维码")
    @GetMapping("createNative/{orderId}")
    public Result createNative(@PathVariable Long orderId){
        Map map=weixinService.createNative(orderId);
        return Result.ok(map);
    }
    //查询支付状态
    /**
    * @Description: 查询支付状态
    * @Param: orderId
    * @return:
    * @Author: cheng
    * @Date: 2022/10/1 9:53
    */
    @GetMapping("queryPayStatus/{orderId}")
    @ApiOperation("查询支付状态")
    public Result queryPayStatus(@PathVariable Long orderId){
        //调用微信的接口实现支付状态查询
        Map<String,String> resultMap = weixinService.queryPayStatus(orderId);
        //判断
        if (resultMap==null){
            return Result.fail().message("支付出错");
        }
        if ("SUCCESS".equals(resultMap.get("trade_state"))){//支付成功
            //更新订单状态
            String otu_trade_no = resultMap.get("out_trade_no");//订单编码
            paymentService.paySuccess(otu_trade_no,resultMap);
            return Result.ok().message("支付成功");
        }
        return Result.ok("支付中");
    }
}
