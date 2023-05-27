package com.cxf.hotel.config;

import com.cxf.hotel.constants.MqConstans;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class DelayedQueueConfig {
    //声明交换机
    @Bean
    public CustomExchange delayedExchange(){
        Map<String,Object> arguments = new HashMap<>();
        arguments.put("x-delayed-type","direct");
        return new CustomExchange(MqConstans.DELAYED_EXCHANGE,"x-delayed-message",true,false,arguments);
    }

    //声明队列
    @Bean
    public Queue delayedQueue(){
        return new Queue(MqConstans.DELAYED_QUEUE);
    }

    //绑定队列
    @Bean
    public Binding delayedQueueBindingDelayedExchange(){
        return BindingBuilder.bind(delayedQueue()).to(delayedExchange()).with(MqConstans.DELAYED_ROUTING_KEY).noargs();
    }
}
