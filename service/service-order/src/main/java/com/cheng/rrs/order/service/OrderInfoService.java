package com.cheng.rrs.order.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.cheng.rrs.model.order.OrderInfo;
import com.cheng.rrs.vo.order.OrderCountQueryVo;
import com.cheng.rrs.vo.order.OrderQueryVo;

import java.util.List;
import java.util.Map;

/**
 * @package: com.cheng.rrs.order.service.impl
 * @Author: cheng
 * @Date: 2022-09-23 10:55
 **/
public interface OrderInfoService extends IService<OrderInfo> {
    //创建订单
    Long saveOrder(String scheduleId, Long patientId);
    //订单列表 条件查询带分页
    IPage<OrderInfo> selectPage(Page<OrderInfo> pageParam, OrderQueryVo orderQueryVo);
    //获取订单详情根据id
    OrderInfo getOrder(String orderId);
    //获取订单
    Map<String,Object> show(Long id);
    //取消预约
    Boolean cancelOrder(Long orderId);
    //就诊通知
    void patientTips();
    //预约统计方法
    Map<String,Object> getCountMap(OrderCountQueryVo orderCountQueryVo);
}
