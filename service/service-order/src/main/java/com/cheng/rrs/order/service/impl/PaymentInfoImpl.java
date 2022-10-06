package com.cheng.rrs.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cheng.rrs.common.exception.YyghException;
import com.cheng.rrs.common.helper.HttpRequestHelper;
import com.cheng.rrs.common.result.ResultCodeEnum;
import com.cheng.rrs.enums.OrderStatusEnum;
import com.cheng.rrs.enums.PaymentStatusEnum;
import com.cheng.rrs.enums.PaymentTypeEnum;
import com.cheng.rrs.hosp.client.HospFeignClient;
import com.cheng.rrs.model.order.OrderInfo;
import com.cheng.rrs.model.order.PaymentInfo;
import com.cheng.rrs.order.mapper.PaymentInfoMapper;
import com.cheng.rrs.order.service.OrderInfoService;
import com.cheng.rrs.order.service.PaymentService;
import com.cheng.rrs.vo.order.SignInfoVo;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * @package: com.cheng.rrs.order.service.impl
 * @Author: cheng
 * @Date: 2022-09-30 10:13
 **/
@Service
public class PaymentInfoImpl extends
        ServiceImpl<PaymentInfoMapper, PaymentInfo> implements PaymentService {

    @Autowired
    private OrderInfoService orderInfoService;
    @Autowired
    private HospFeignClient hospFeignClient;
    //向支付记录表添加信息
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void savePaymentInfo(OrderInfo orderInfo, Integer paymentType) {
        //根据订单id和支付类型，查询支付记录表是否存在相同订单
        QueryWrapper<PaymentInfo> wrapper=new QueryWrapper<>();
        wrapper.eq("order_id",orderInfo.getId());
        wrapper.eq("payment_type",paymentType);
        Long count = baseMapper.selectCount(wrapper);
        if (count>0){
            return;
        }
        //添加记录
        PaymentInfo paymentInfo=new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(orderInfo.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setOutTradeNo(orderInfo.getOutTradeNo());
        paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());
        String subject = new DateTime(orderInfo.getReserveDate())
                .toString("yyyy-MM-dd")+"|"+
                orderInfo.getHosname()+"|"+
                orderInfo.getDepname()+"|"+
                orderInfo.getTitle();
        paymentInfo.setSubject(subject);
        paymentInfo.setTotalAmount(orderInfo.getAmount());
        baseMapper.insert(paymentInfo);
    }

    //更新订单状态
    @Override
    @Transactional(rollbackFor = Exception.class)
    public void paySuccess(String otu_trade_no, Map<String, String> resultMap) {
        //1 根据订单编号 得到支付记录
        QueryWrapper<PaymentInfo> wrapper=new QueryWrapper<>();
        wrapper.eq("out_trade_no",otu_trade_no);
        wrapper.eq("payment_type", PaymentTypeEnum.WEIXIN.getStatus());
        PaymentInfo paymentInfo = baseMapper.selectOne(wrapper);
        //2 更新支付记录信息
        paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
        paymentInfo.setCallbackTime(new Date());
        paymentInfo.setTradeNo(resultMap.get("transaction_id"));
        paymentInfo.setCallbackContent(resultMap.toString());
        baseMapper.updateById(paymentInfo);
        //3 根据订单编号得到订单信息
        OrderInfo orderInfo = orderInfoService.getById(paymentInfo.getOrderId());
        //4 更新订单信息
        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        orderInfoService.updateById(orderInfo);
        //5 调用医院接口 更新订单支付信息
        SignInfoVo signInfoVo = hospFeignClient.getSignInfoVo(orderInfo.getHoscode());
        Map<String,Object> resultMaps=new HashMap<>();
        resultMaps.put("hoscode",orderInfo.getHoscode());
        resultMaps.put("hosRecodrId",orderInfo.getHosRecordId());
        resultMaps.put("timeStamp", HttpRequestHelper.getTimestamp());
        String sign = HttpRequestHelper.getSign(resultMaps, signInfoVo.getSignKey());
        resultMaps.put("sign",sign);

        JSONObject result = HttpRequestHelper.sendRequest(resultMaps, signInfoVo.getApiUrl() + "/order/updatePayStatus");


    }

    //获取支付记录
    @Override
    public PaymentInfo getPaymentInfo(Long orderId, Integer paymentType) {
        QueryWrapper<PaymentInfo> wrapper=new QueryWrapper<>();
        wrapper.eq("order_id",orderId);
        wrapper.eq("payment_type",paymentType);
        PaymentInfo paymentInfo = baseMapper.selectOne(wrapper);
        return paymentInfo;
    }
}
