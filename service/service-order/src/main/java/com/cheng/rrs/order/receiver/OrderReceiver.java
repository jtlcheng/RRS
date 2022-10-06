package com.cheng.rrs.order.receiver;

import com.cheng.rrs.order.service.OrderInfoService;
import com.cheng.rrs.rabbit.constant.MqConst;
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
 * @package: com.cheng.rrs.order.receiver
 * @Author: cheng
 * @Date: 2022-10-03 10:19
 **/
@Component
public class OrderReceiver {

    @Autowired
    private OrderInfoService orderInfoService;


    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = MqConst.QUEUE_TASK_8, durable = "true"),
            exchange = @Exchange(value = MqConst.EXCHANGE_DIRECT_TASK),
            key = {MqConst.ROUTING_TASK_8}
    ))
    public void patientTips(Message message, Channel channel) throws IOException {
            orderInfoService.patientTips();
    }

}
