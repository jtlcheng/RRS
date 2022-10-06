package com.cheng.rrs.task.schedule;

import com.cheng.rrs.rabbit.constant.MqConst;
import com.cheng.rrs.rabbit.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @package: com.cheng.rrs.task.schedule
 * @Author: cheng
 * @Date: 2022-10-03 10:13
 **/
@Component
@EnableScheduling
public class ScheduleTask {
    @Autowired
    private RabbitService rabbitService;
    //每天八点执行方法 提醒就医
    //cron 表达式 设置执行间隔
    @Scheduled(cron = "0 30 7 * * ? ")
    public void taskPatient(){
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK,MqConst.ROUTING_TASK_8,"");
    }
}
