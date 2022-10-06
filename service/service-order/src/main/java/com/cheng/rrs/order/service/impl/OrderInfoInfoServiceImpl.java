package com.cheng.rrs.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cheng.rrs.common.exception.YyghException;
import com.cheng.rrs.common.helper.HttpRequestHelper;
import com.cheng.rrs.common.result.ResultCodeEnum;

import com.cheng.rrs.enums.OrderStatusEnum;
import com.cheng.rrs.hosp.client.HospFeignClient;
import com.cheng.rrs.model.hosp.Schedule;
import com.cheng.rrs.model.order.OrderInfo;
import com.cheng.rrs.model.user.Patient;
import com.cheng.rrs.order.mapper.OrderInfoMapper;
import com.cheng.rrs.order.service.OrderInfoService;
import com.cheng.rrs.order.service.WeixinService;
import com.cheng.rrs.rabbit.constant.MqConst;
import com.cheng.rrs.rabbit.service.RabbitService;
import com.cheng.rrs.user.client.PatientFeignClient;
import com.cheng.rrs.vo.hosp.ScheduleOrderVo;
import com.cheng.rrs.vo.msm.MsmVo;
import com.cheng.rrs.vo.order.*;
import org.joda.time.DateTime;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

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
    @Autowired
    private WeixinService weixinService;


    //创建订单
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long saveOrder(String scheduleId, Long patientId) {
        //获取就诊人信息
        Patient patient = patientFeignClient.getPatientOrder(patientId);
        System.out.println("patient:" + patient);
        if (patient == null) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //根据排班id获取预约下单数据
        ScheduleOrderVo scheduleOrderVo = hospFeignClient.getScheduleOrderVo(scheduleId);
        System.out.println("scheduleOrderVo:" + scheduleOrderVo);
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
        System.out.println("医院签名:" + signInfoVo);
        if (scheduleOrderVo == null) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        //可预约数
        if (scheduleOrderVo.getAvailableNumber() <= 0) {
            throw new YyghException(ResultCodeEnum.NUMBER_NO);
        }
        OrderInfo orderInfo = new OrderInfo();
        System.out.println("scheduleOrderVo:" + scheduleOrderVo);
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
        System.out.println("orderInfo:" + orderInfo);
        baseMapper.insert(orderInfo);
//        this.save(orderInfo);

        //调用医院接口 实现预约挂号操作
        //设置调用医院接口需要参数 参数放到map集合
        Map<String, Object> paramMap = new HashMap<>();
        paramMap.put("userId", orderInfo.getUserId());
        paramMap.put("hoscode", orderInfo.getHoscode());
        paramMap.put("depcode", orderInfo.getDepcode());
        paramMap.put("depname", orderInfo.getDepname());
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
        System.out.println("paramMap:" + paramMap);
        //请求医院系统接口
//        MD5.encrypt(signInfoVo.getSignKey());
        JSONObject result = HttpRequestHelper.sendRequest(paramMap, signInfoVo.getApiUrl() + "/order/submitOrder");
        System.out.println("resultsada:" + result);
        System.out.println("result.getInteger：" + result.getString("code"));
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
            System.out.println(
                    " baseMapper.updateById(orderInfo)" + orderInfo
            );
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

    //订单列表 条件查询带分页
    @Override
    public IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo) {

        this.getDate();

        //orderQueryVo 获取条件值
        String hosname = orderQueryVo.getHosname();//医院名称
        Long userId = orderQueryVo.getUserId();//会员id
        Long patientId = orderQueryVo.getPatientId();//就诊人id
        String patientName = orderQueryVo.getPatientName();//就诊人名称
        String orderStatus = orderQueryVo.getOrderStatus();//订单状态
        String reserveDate = orderQueryVo.getReserveDate();//安排时间
        String createTimeBegin = orderQueryVo.getCreateTimeBegin();//创建时间开始
        String createTimeEnd = orderQueryVo.getCreateTimeEnd();//创建时间结束


        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        if (!StringUtils.isEmpty(userId)) {
            wrapper.eq("user_id", userId);
        }
        if (!StringUtils.isEmpty(hosname)) {
            wrapper.like("hosname", hosname);
        }
        if (!StringUtils.isEmpty(patientName)) {
            wrapper.eq("patient_name", patientName);
        }
        if (!StringUtils.isEmpty(patientId)) {
            wrapper.eq("patient_id", patientId);
        }
        if (!StringUtils.isEmpty(orderStatus)) {
            wrapper.eq("order_status", orderStatus);
        }
        if (!StringUtils.isEmpty(reserveDate)) {
            wrapper.ge("reserve_date", reserveDate);
        }
        if (!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time", createTimeBegin);
        }
        if (!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time", createTimeEnd);
        }

        System.out.println("wrapper:" + wrapper);
        //调用mapper方法
        IPage<OrderInfo> pages = baseMapper.selectPage(pageParam, wrapper);


        System.out.println("pages:" + pages);
        //编号变成对应值封装
        pages.getRecords().stream().forEach(item -> {
            this.packOrderInfo(item);
        });
        return pages;
    }

    //获取订单详情根据id
    @Override
    public OrderInfo getOrder(String orderId) {
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        return this.packOrderInfo(orderInfo);
    }

    //获取订单
    @Override
    public Map<String, Object> show(Long id) {
        Map<String, Object> map = new HashMap<>();
        OrderInfo orderInfo = this.packOrderInfo(this.getById(id));
        map.put("orderInfo", orderInfo);
        Patient patient =
                patientFeignClient.getPatientOrder(orderInfo.getPatientId());
        map.put("patient", patient);
        return map;
    }

    //取消预约
    @Override
    public Boolean cancelOrder(Long orderId) {
        //获取订单信息
        OrderInfo orderInfo = baseMapper.selectById(orderId);
        //判断是否取消
        DateTime quitTime = new DateTime(orderInfo.getQuitTime());
        if (quitTime.isBeforeNow()) {
            throw new YyghException(ResultCodeEnum.CANCEL_ORDER_NO);
        }
        //调用医院接口实现预约取消
        SignInfoVo signInfoVo = hospFeignClient.getSignInfoVo(orderInfo.getHoscode());
        if (null == signInfoVo) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("hoscode", orderInfo.getHoscode());
        reqMap.put("hosRecordId", orderInfo.getHosRecordId());
        reqMap.put("timestamp", HttpRequestHelper.getTimestamp());
//        String sign = HttpRequestHelper.getSign(reqMap, signInfoVo.getSignKey());

        reqMap.put("sign", signInfoVo.getSignKey());
        JSONObject result = HttpRequestHelper.sendRequest(reqMap, signInfoVo.getApiUrl() + "/order/updateCancelStatus");

        //根据医院接口返回数据
        if (result.getInteger("code") != 200) {
            throw new YyghException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
        } else {
            //判断没付款的订单取消预约
            if (orderInfo.getOrderStatus().intValue()
                    == OrderStatusEnum.UNPAID.getStatus().intValue()) {
                orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
                baseMapper.updateById(orderInfo);

                //发送mq信息更新预约数 我们与下单成功更新预约数使用相同的mq信息，不设置可预约数与剩余预约数，接收端可预约数加1即可
                OrderMqVo orderMqVo = new OrderMqVo();
                orderMqVo.setScheduleId(orderInfo.getScheduleId());
                //短信提示
                MsmVo msmVo = new MsmVo();
                msmVo.setPhone(orderInfo.getPatientPhone());
//                msmVo.setTemplateCode("SMS_154950909");
                String reserveData =
                        new DateTime(orderInfo.getReserveDate()).
                                toString("yyyy-MM-dd") + (orderInfo.getReserveTime() == 0 ? "上午" : "下午");
                System.out.println("reserveData:" + reserveData);
                System.out.println("OrderInfoServiceImpl+orderInfo:" + orderInfo);
                Map<String, Object> paramMap = new HashMap<String, Object>() {{
                    put("title", orderInfo.getHosname() + "|" + orderInfo.getDepname() + "|" + orderInfo.getTitle());
                    put("reserveData", reserveData);
                    put("name", orderInfo.getPatientName());
                }};
                msmVo.setParam(paramMap);
                orderMqVo.setMsmVo(msmVo);
                rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
            }
            //判断当前的订单是否可以取消
            if (orderInfo.getOrderStatus().intValue() == OrderStatusEnum.PAID.getStatus().intValue()) {
                Boolean isRefund = weixinService.refund(orderId);
                if (!isRefund) {
                    throw new YyghException(ResultCodeEnum.CANCEL_ORDER_FAIL);
                }
                //更新订单状态
                orderInfo.setOrderStatus(OrderStatusEnum.CANCLE.getStatus());
                baseMapper.updateById(orderInfo);

                //发送mq信息更新预约数 我们与下单成功更新预约数使用相同的mq信息，不设置可预约数与剩余预约数，接收端可预约数加1即可
                OrderMqVo orderMqVo = new OrderMqVo();
                orderMqVo.setScheduleId(orderInfo.getScheduleId());
                //短信提示
                MsmVo msmVo = new MsmVo();
                msmVo.setPhone(orderInfo.getPatientPhone());
//                msmVo.setTemplateCode("SMS_154950909");
                String reserveData =
                        new DateTime(orderInfo.getReserveDate()).
                                toString("yyyy-MM-dd") + (orderInfo.getReserveTime() == 0 ? "上午" : "下午");
                System.out.println("reserveData:" + reserveData);
                System.out.println("OrderInfoServiceImpl+orderInfo:" + orderInfo);
                Map<String, Object> paramMap = new HashMap<String, Object>() {{
                    put("title", orderInfo.getHosname() + "|" + orderInfo.getDepname() + "|" + orderInfo.getTitle());
                    put("reserveData", reserveData);
                    put("name", orderInfo.getPatientName());
                }};
                msmVo.setParam(paramMap);
                orderMqVo.setMsmVo(msmVo);
                rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_ORDER, MqConst.ROUTING_ORDER, orderMqVo);
            }
            return true;
        }
    }

    //就诊通知
    @Override
    public void patientTips() {
        QueryWrapper<OrderInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("reserve_date", new DateTime().toString("yyyy-MM-dd"));
        wrapper.ne("order_status",OrderStatusEnum.CANCLE.getStatus()).ne("order_status",OrderStatusEnum.OVERDUE.getStatus());

        List<OrderInfo> orderInfoList = baseMapper.selectList(wrapper);
        for (OrderInfo orderInfo : orderInfoList) {
            //短信提示
            MsmVo msmVo = new MsmVo();
            msmVo.setPhone(orderInfo.getPatientPhone());
            String reserveDate = new DateTime(orderInfo.getReserveDate()).toString("yyyy-MM-dd") + (orderInfo.getReserveTime() == 0 ? "上午" : "下午");
            Map<String, Object> param = new HashMap<String, Object>() {{
                put("title", orderInfo.getHosname() + "|" + orderInfo.getDepname() + "|" + orderInfo.getTitle());
                put("reserveDate", reserveDate);
                put("name", orderInfo.getPatientName());
            }};
            msmVo.setParam(param);
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msmVo);
        }
    }

    //预约统计
    @Override
    public Map<String, Object> getCountMap(OrderCountQueryVo orderCountQueryVo) {
        //调用mapper方法得到数据
        List<OrderCountVo> orderCountVoList = baseMapper.selectOrderCount(orderCountQueryVo);
        //获取x需要数据，日期数据 list集合
        List<String> dateList = orderCountVoList.stream().
                map(OrderCountVo::getReserveDate).collect(Collectors.toList());
        //获取y需要数据 ，具体数据 list集合
        List<Integer> countList = orderCountVoList.stream().
                map(OrderCountVo::getCount).collect(Collectors.toList());

        Map<String,Object> map=new HashMap<>();
        map.put("dateList",dateList);
        map.put("countList",countList);
        return map;
    }


    public List<OrderInfo> getDate() {
        Date date = new Date();
        Calendar calendar = new GregorianCalendar();
        calendar.setTime(date);
        calendar.add(Calendar.DATE, 1);
        date = calendar.getTime();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String format = simpleDateFormat.format(date);
        List<OrderInfo> orderInfos = null;
        try {
            Date parse = simpleDateFormat.parse(format);
            orderInfos =
                    baseMapper.selectList(new LambdaQueryWrapper<OrderInfo>().select(OrderInfo::getId, OrderInfo::getReserveDate));
            for (OrderInfo orderInfo : orderInfos) {
                if (orderInfo.getReserveDate().compareTo(parse) < 0) {
                    System.out.println("orderInfos:" + orderInfos);
                    System.out.println("orderInfos:" + orderInfo.getId());
                    orderInfo.setId(orderInfo.getId());
                    orderInfo.setOrderStatus(3);
                    baseMapper.updateById(orderInfo);
                }
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return orderInfos;
    }


    private OrderInfo packOrderInfo(OrderInfo orderInfo) {
        orderInfo.getParam().
                put("orderStatusString", OrderStatusEnum.getStatusNameByStatus(orderInfo.getOrderStatus()));
        return orderInfo;
    }
}
