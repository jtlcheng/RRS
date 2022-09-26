package com.cheng.rrs.hosp.receiver;

import com.cheng.rrs.hosp.service.ScheduleService;
import com.cheng.rrs.model.hosp.Schedule;
import com.cheng.rrs.rabbit.constant.MqConst;
import com.cheng.rrs.rabbit.service.RabbitService;
import com.cheng.rrs.vo.msm.MsmVo;
import com.cheng.rrs.vo.order.OrderMqVo;
import com.rabbitmq.client.Channel;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * @package: com.cheng.rrs.hosp.receiver
 * @Author: cheng
 * @Date: 2022-09-24 16:28
 **/
@Component
public class HospitalReceiver {
    @Autowired
    private ScheduleService scheduleService;
    @Autowired
    private RabbitService rabbitService;

    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_ORDER, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_ORDER),
            key = {MqConst.ROUTING_ORDER}
    ))
    public void receiver(OrderMqVo orderMqVo, Message message, Channel channel) throws IOException {
        if (null!=orderMqVo.getAvailableNumber()){
            //下单成功更新预约数
            Schedule schedule = scheduleService.getById(orderMqVo.getScheduleId());
            schedule.setReservedNumber(orderMqVo.getReservedNumber());
            schedule.setAvailableNumber(orderMqVo.getAvailableNumber());
            scheduleService.update(schedule);
        }else {
            Schedule schedule = scheduleService.getById(orderMqVo.getScheduleId());
            int availableNumber=schedule.getAvailableNumber().intValue()+ 1;
            schedule.setAvailableNumber(availableNumber);
            scheduleService.update(schedule);
        }

        //发送短信
        MsmVo msmVo = orderMqVo.getMsmVo();
        if(null != msmVo) {
            rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_MSM, MqConst.ROUTING_MSM_ITEM, msmVo);
        }
    }

}
