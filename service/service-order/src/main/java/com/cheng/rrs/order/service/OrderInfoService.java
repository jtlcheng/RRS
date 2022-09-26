package com.cheng.rrs.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cheng.rrs.model.order.OrderInfo;

/**
 * @package: com.cheng.rrs.order.service.impl
 * @Author: cheng
 * @Date: 2022-09-23 10:55
 **/
public interface OrderInfoService extends IService<OrderInfo> {
    //创建订单
    Long saveOrder(String scheduleId, Long patientId);
}
