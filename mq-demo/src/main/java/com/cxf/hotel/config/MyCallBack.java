package com.cxf.hotel.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;

//@Component
@Slf4j
public class MyCallBack implements RabbitTemplate.ConfirmCallback,RabbitTemplate.ReturnCallback{

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @PostConstruct
    public void init(){
        rabbitTemplate.setConfirmCallback(this);
        rabbitTemplate.setReturnCallback(this);
    }

    @Override
    public void confirm(CorrelationData correlationData, boolean b, String s) {
        String id = correlationData != null ? correlationData.getId() : "";
        if (b){
            log.info("接收到ID为：{}的消息",id);
        }else {
            log.info("未接受到ID为：{}的消息，发生原因：{}",id,s);
            //填写具体的业务代码比如插入到数据库
        }
    }

    @Override
    public void returnedMessage(Message message, int replyCode, String replyText, String exchange, String routingKey) {
        // 投递失败，记录日志
        log.info("消息发送失败，应答码{}，原因{}，交换机{}，路由键{},消息{}",
                replyCode, replyText, exchange, routingKey, message.toString());
        // 如果有业务需要，可以重发消息
    }
}
