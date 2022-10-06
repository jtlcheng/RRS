package com.cheng.rrs.order.service;

import java.util.Map;

/**
 * @package: com.cheng.rrs.order.service
 * @Author: cheng
 * @Date: 2022-09-30 09:56
 **/
public interface WeixinService {
    //生成微信支付二维码
    Map createNative(Long orderId);
    ////调用微信的接口实现支付状态查询
    Map<String, String> queryPayStatus(Long orderId);
    //退款
    Boolean refund(Long orderId);
}
