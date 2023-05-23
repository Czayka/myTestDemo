package com.cxf.hotel.config;

import com.cxf.hotel.constants.MqConstans;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MqConfig {

    @Bean
    public TopicExchange topicExchange(){
        return new TopicExchange(MqConstans.HOTEL_EXCHANGE,true,false);
    }
    @Bean
    public Queue insertQueue(){
        return new Queue(MqConstans.HOTEL_INSERT_QUEUE,true);
    }
    @Bean
    public Queue deleteQueue(){
        return new Queue(MqConstans.HOTEL_DELETE_QUEUE,true);
    }
    @Bean
    public Binding insertQueueBinding(){
        return BindingBuilder.bind(insertQueue()).to(topicExchange()).with(MqConstans.HOTEL_INSERT_KEY);
    }

    @Bean
    public Binding deleteQueueBinding(){
        return BindingBuilder.bind(deleteQueue()).to(topicExchange()).with(MqConstans.HOTEL_DELETE_KEY);
    }
}
