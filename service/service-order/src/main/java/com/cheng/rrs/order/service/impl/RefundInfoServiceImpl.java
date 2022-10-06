package com.cheng.rrs.order.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cheng.rrs.enums.RefundStatusEnum;
import com.cheng.rrs.model.order.PaymentInfo;
import com.cheng.rrs.model.order.RefundInfo;
import com.cheng.rrs.order.mapper.RefundInfoMapper;
import com.cheng.rrs.order.service.RefundInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;

/**
 * @package: com.cheng.rrs.order.service.impl
 * @Author: cheng
 * @Date: 2022-10-02 11:38
 **/
@Service
public class RefundInfoServiceImpl extends
        ServiceImpl<RefundInfoMapper, RefundInfo> implements RefundInfoService {


    //保存退款记录
    @Override
    public RefundInfo saveRefundInfo(PaymentInfo paymentInfo) {
        QueryWrapper<RefundInfo> wrapper=new QueryWrapper();
        wrapper.eq("order_id",paymentInfo.getOrderId());
        wrapper.eq("payment_type",paymentInfo.getPaymentType());
        RefundInfo refundInfo = baseMapper.selectOne(wrapper);
        if (refundInfo!=null){//有相同数据
            return  refundInfo;
        }
        //添加记录
        refundInfo=new RefundInfo();
        refundInfo.setCreateTime(new Date());
        refundInfo.setOrderId(paymentInfo.getOrderId());
        refundInfo.setPaymentType(paymentInfo.getPaymentType());
        refundInfo.setOutTradeNo(paymentInfo.getOutTradeNo());
        refundInfo.setRefundStatus(RefundStatusEnum.UNREFUND.getStatus());
        refundInfo.setSubject(paymentInfo.getSubject());
        //paymentInfo.setSubject("test");
        refundInfo.setTotalAmount(paymentInfo.getTotalAmount());
        baseMapper.insert(refundInfo);
        return refundInfo;

    }
}
