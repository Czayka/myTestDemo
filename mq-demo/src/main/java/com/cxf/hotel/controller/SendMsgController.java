package com.cxf.hotel.controller;

import com.cxf.hotel.config.RabbitMQConfig;
import com.cxf.hotel.constants.MqConstans;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.core.MessageProperties;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@RestController
@RequestMapping("/ttl")
@Slf4j
public class SendMsgController {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/message/{message}")
    public void senMessage(@PathVariable String message){
        log.info("当前时间{}，发送一条信息给两个TTL队列：{}",new Date().toString(),message);

        rabbitTemplate.convertAndSend("X","XA","消息來之ttl为10s的队列"+message);
        rabbitTemplate.convertAndSend("X","XB","消息來之ttl为40s的队列"+message);
    }

    @GetMapping("/message/{message}/{ttlTime}")
    public void senMessage(@PathVariable String message,@PathVariable String ttlTime){
        log.info("当前时间{}，发送一条时长{}毫秒TTL信息给死信队列：{}",new Date().toString(),ttlTime,message);
//        MessageProperties messageProperties = new MessageProperties();
//        messageProperties.setExpiration(ttlTime);
//        Message build = MessageBuilder
//                .withBody(message.getBytes())
//                .andProperties(messageProperties)
//                .build();
//        rabbitTemplate.convertAndSend(MqConstans.MY_EXCHANGE,MqConstans.MY_ROUTING_KEY,build);
        rabbitTemplate.convertAndSend(MqConstans.MY_EXCHANGE,MqConstans.MY_ROUTING_KEY,"消息来自ttl:"+message, msg->{
            msg.getMessageProperties().setExpiration(ttlTime);
            return msg;
        });
    }

    @GetMapping("/sendDelayedMessage/{message}/{delayTime}")
    public void sendDelayedMessage(@PathVariable String message,@PathVariable Integer delayTime){
        log.info("当前时间{}，发送一条时长{}毫秒TTL信息给延迟队列delayed.queue：{}",
                new Date().toString(),delayTime,message);
        rabbitTemplate.convertAndSend(MqConstans.DELAYED_EXCHANGE,MqConstans.DELAYED_ROUTING_KEY,"消息来自ttl:"+message, msg->{
            msg.getMessageProperties().setDelay(delayTime);
            return msg;
        });
    }

    @GetMapping("/sendConfirm/{message}")
    public void sendConfirm(@PathVariable String message){
        CorrelationData correlationData1 = new CorrelationData();
        correlationData1.setId("1");

        rabbitTemplate.convertAndSend(RabbitMQConfig.CONFIRM_EXCHANGE_NAME,
                RabbitMQConfig.CONFIRM_EXCHANGE_ROUTING_KEY,message+" key1",correlationData1);
        log.info("发送消息内容为：{}",message+" key1");

        CorrelationData correlationData2 = new CorrelationData();
        correlationData2.setId("2");
        correlationData2.setReturnedMessage(new Message(
                message.getBytes(),null
        ));
        rabbitTemplate.convertAndSend(RabbitMQConfig.CONFIRM_EXCHANGE_NAME + "123456",
                RabbitMQConfig.CONFIRM_EXCHANGE_ROUTING_KEY,message+" key2",correlationData2);
        log.info("发送消息内容为：{}",message+" key2");

        CorrelationData correlationData3 = new CorrelationData();
        correlationData3.setId("3");
        rabbitTemplate.convertAndSend(RabbitMQConfig.CONFIRM_EXCHANGE_NAME,
                RabbitMQConfig.CONFIRM_EXCHANGE_ROUTING_KEY + "123456",message+" key3",correlationData3);
        log.info("发送消息内容为：{}",message+" key3");
    }
}
