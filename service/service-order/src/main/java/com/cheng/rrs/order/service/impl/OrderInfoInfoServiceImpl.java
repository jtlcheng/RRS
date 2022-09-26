package com.cheng.rrs.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cheng.rrs.common.exception.YyghException;
import com.cheng.rrs.common.helper.HttpRequestHelper;
import com.cheng.rrs.common.result.ResultCodeEnum;
import com.cheng.rrs.common.utils.MD5;
import com.cheng.rrs.enums.OrderStatusEnum;
import com.cheng.rrs.hosp.client.HospFeignClient;
import com.cheng.rrs.model.order.OrderInfo;
import com.cheng.rrs.model.user.Patient;
import com.cheng.rrs.order.mapper.OrderInfoMapper;
import com.cheng.rrs.order.service.OrderInfoService;
import com.cheng.rrs.rabbit.constant.MqConst;
import com.cheng.rrs.rabbit.service.RabbitService;
import com.cheng.rrs.user.client.PatientFeignClient;
import com.cheng.rrs.vo.hosp.ScheduleOrderVo;
import com.cheng.rrs.vo.msm.MsmVo;
import com.cheng.rrs.vo.order.OrderMqVo;
import com.cheng.rrs.vo.order.SignInfoVo;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @package: com.cheng.rrs.order.service
 * @Author: cheng
 * @Date: 2022-09-23 10:55
 **/
@Service
public class OrderInfoInfoServiceImpl extends ServiceImpl<OrderInfoMapper, OrderInfo> implements OrderInfoService {

    @Autowired
    private PatientFeignClient patientFeignClient;
    @Autowired
    private HospFeignClient hospFeignClient;
    @Autowired
    private RabbitService rabbitService;

    //创建订单
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrder(String scheduleId, Long patientId) {
        //获取就诊人信息
        Patient patient = patientFeignClient.getPatientOrder(patientId);
        System.out.println("patient:"+patient);
        if (patient == null) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //根据排班id获取预约下单数据
        ScheduleOrderVo scheduleOrderVo = hospFeignClient.getScheduleOrderVo(scheduleId);
        System.out.println("scheduleOrderVo:"+scheduleOrderVo);
        if (scheduleOrderVo == null) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //当前时间不可以预约
        if (new DateTime(scheduleOrderVo.getStartTime()).isAfterNow() ||
                new DateTime(scheduleOrderVo.getEndTime()).isBeforeNow()) {
            throw new YyghException(ResultCodeEnum.TIME_NO);
        }
        //获取医院签名信息
        SignInfoVo signInfoVo = hospFeignClient.getSignInfoVo(scheduleOrderVo.getHoscode());
        System.out.println("医院签名:"+signInfoVo);
        if (scheduleOrderVo == null) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //可预约数
        if (scheduleOrderVo.getAvailableNumber() <= 0) {
            throw new YyghException(ResultCodeEnum.NUMBER_NO);
        }
        OrderInfo orderInfo = new OrderInfo();
        //scheduleOrderVo复制到OrderInfo中
        BeanUtils.copyProperties(scheduleOrderVo, orderInfo);
        String outTradeNo = System.currentTimeMillis() + "" + new Random().nextInt(100);
        orderInfo.setOutTradeNo(outTradeNo);
        orderInfo.setScheduleId(scheduleId);
        orderInfo.setUserId(patient.getUserId());
        orderInfo.setPatientId(patientId);
        orderInfo.setPatientName(patient.getName());
        orderInfo.setPatientPhone(patient.getPhone());
        orderInfo.setOrderStatus(OrderStatusEnum.UNPAID.getStatus());
        baseMapper.insert(orderInfo);
//           this.save(orderInfo);

        //调用医院接口 实现预约挂号操作
        //设置调用医院接口需要参数 参数放到map集合
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId",orderInfo.getUserId());
        paramMap.put("hoscode", orderInfo.getHoscode());
        paramMap.put("depcode", orderInfo.getDepcode());
        paramMap.put("depname",orderInfo.getDepname());
        paramMap.put("hosScheduleId", orderInfo.getScheduleId());
        paramMap.put("reserveDate", new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd"));
        paramMap.put("reserveTime", orderInfo.getReserveTime());
        paramMap.put("amount", orderInfo.getAmount());
        paramMap.put("name", patient.getName());
        paramMap.put("certificatesType", patient.getCertificatesType());
        paramMap.put("certificatesNo", patient.getCertificatesNo());
        paramMap.put("sex", patient.getSex());
        paramMap.put("birthdate", patient.getBirthdate());
        paramMap.put("phone", patient.getPhone());
        paramMap.put("isMarry", patient.getIsMarry());
        paramMap.put("provinceCode", patient.getProvinceCode());
        paramMap.put("cityCode", patient.getCityCode());
        paramMap.put("districtCode", patient.getDistrictCode());
        paramMap.put("address", patient.getAddress());

        //联系人
        paramMap.put("contactsName", patient.getContactsName());
        paramMap.put("contactsCertificatesType", patient.getContactsCertificatesType());
        paramMap.put("contactsCertificatesNo", patient.getContactsCertificatesNo());
        paramMap.put("contactsPhone", patient.getContactsPhone());
        paramMap.put("timestamp", HttpRequestHelper.getTimestamp());
//        String sign = com.cheng.rrs.common.helper.HttpRequestHelper.getSign(paramMap, signInfoVo.getSignKey());
//        System.out.println("sign:"+sign);
        paramMap.put("sign", signInfoVo.getSignKey());
        System.out.println("paramMap:"+paramMap);
        //请求医院系统接口
//        MD5.encrypt(signInfoVo.getSignKey());
        JSONObject result = HttpRequestHelper.sendRequest(paramMap, signInfoVo.getApiUrl() + "/order/submitOrder");
        System.out.println("resultsada:"+result);
        if (result.getInteger("code") == 200) {
            JSONObject jsonObject = result.getJSONObject("data");
            //预约记录唯一标识(医院预约记录主键)
            String hosRecordId = jsonObject.getString("hosRecordId");
            //预约序号
            Integer number = jsonObject.getInteger("number");
            //取号时间
            String fetchTime = jsonObject.getString("fetchTime");
            //取号地址
            String fetchAddress = jsonObject.getString("fetchAddress");
            //更新订单
            orderInfo.setHosRecordId(hosRecordId);
            orderInfo.setNumber(number);
            orderInfo.setFetchTime(fetchTime);
            orderInfo.setFetchAddress(fetchAddress);
            baseMapper.updateById(orderInfo);

            //排班可预约数
            Integer reservedNumber = jsonObject.getInteger("reservedNumber");
            //排班剩余预约数
            Integer availableNumber = jsonObject.getInteger("availableNumber");
            //发送mq 信息更新号源和 短信通知
            //发送mq号源更新
            OrderMqVo orderMqVo = new OrderMqVo();
            orderMqVo.setScheduleId(scheduleId);
            orderMqVo.setReservedNumber(reservedNumber);
            orderMqVo.setAvailableNumber(availableNumber);

            //短信提示
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
//            msmVo.setTemplateCode("SMS_154950909");
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") +
                    (orderInfo.getReserveTime() == 0 ? "上午" : "下午");
            Map<String, Object> param = new HashMap<String, Object>() {{
                put("title", orderInfo.getHosname() + "|" + orderInfo.getDepname() + "|" + orderInfo.getTitle());
                put("amount", orderInfo.getAmount());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
                put("quitTime", new DateTime(orderInfo.getQuitTime()).toString("yyyy-MM-dd HH:mm"));
            }};
            msmVo.setParam(param);
            orderMqVo.setMsmVo(msmVo);
            //发送
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
        } else {
            throw new YyghException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        }
        return orderInfo.getId();
    }
}
