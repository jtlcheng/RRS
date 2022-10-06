package com.cheng.rrs.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cheng.rrs.model.order.OrderInfo;
import com.cheng.rrs.model.order.PaymentInfo;

import java.util.Map;

/**
 * @package: com.cheng.rrs.order.service
 * @Author: cheng
 * @Date: 2022-09-30 10:13
 **/
public interface PaymentService extends IService<PaymentInfo> {
    //向支付记录表添加信息
    void savePaymentInfo(OrderInfo orderInfo, Integer status);
    //更新订单状态
    void paySuccess(String otu_trade_no, Map<String, String> resultMap);
    //获取支付记录
    /**
    * @Description: 获取支付记录
    * @Param: orderId PaymentType
    * @return:
    * @Author: cheng
    * @Date: 2022/10/2 11:32
    */
    PaymentInfo getPaymentInfo(Long orderId, Integer PaymentType);
}
