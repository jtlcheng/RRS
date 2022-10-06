package com.cheng.rrs.order.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cheng.rrs.model.order.PaymentInfo;
import com.cheng.rrs.model.order.RefundInfo;

/**
 * @package: com.cheng.rrs.order.service
 * @Author: cheng
 * @Date: 2022-10-02 11:38
 **/
public interface RefundInfoService extends IService<RefundInfo> {
    //保存退款记录
    /**
    * @Description: 保存退款记录
    * @Param: paymentInfo
    * @return:
    * @Author: cheng
    * @Date: 2022/10/2 11:41
    */
    RefundInfo saveRefundInfo(PaymentInfo paymentInfo);
}
