package com.cxf.hotel.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

//@Configuration
public class TtlQueueConfig {

    //普通交换机名称
    public static final String X_EXCHANGE = "X";
    //死信交换机名称
    public static final String Y_DEAD_LETTER_EXCHANGE = "Y";
    //普通队列名称
    public static final String QUEUE_A = "QA";
    public static final String QUEUE_B = "QB";
    public static final String QUEUE_C = "QC";
    //死信队列名称
    public static final String DEAD_LETTER_QUEUE = "QD";
    //死信RoutingKey名称
    public static final String DEAD_LETTER_ROUTING_KEY = "YD";

    //声明xExchange
    @Bean
    public TopicExchange xExchange(){
        return new TopicExchange(X_EXCHANGE,true,false);
    }
    //声明yExchange
    @Bean
    public TopicExchange yExchange(){
        return new TopicExchange(Y_DEAD_LETTER_EXCHANGE,true,false);
    }

    //声明普通队列TTL为10秒
    @Bean
    public Queue queueA(){
//        Map<String,Object> arguments = new HashMap<>();
//        //设置死信交换机
//        arguments.put("x-dead-letter-exchange",Y_DEAD_LETTER_EXCHANGE);
//        //设置死信RoutingKey
//        arguments.put("x-dead-letter-routing-key",DEAD_LETTER_ROUTING_KEY);
//        //设置过期时间 单位是ms
//        arguments.put("x-message-ttl",10000);
//        return QueueBuilder
//                .durable(QUEUE_A)
//                .withArguments(arguments)
//                .build();
        return QueueBuilder
                .durable(QUEUE_A)
                .ttl(10000) //设置过期时间 单位是ms
                .deadLetterExchange(Y_DEAD_LETTER_EXCHANGE) //设置死信交换机
                .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY) //设置死信RoutingKey
                .build();
    }

    //声明普通队列TTL为40秒
    @Bean
    public Queue queueB(){
        return QueueBuilder
                .durable(QUEUE_B)
                .ttl(40000) //设置过期时间 单位是ms
                .deadLetterExchange(Y_DEAD_LETTER_EXCHANGE) //设置死信交换机
                .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY) //设置死信RoutingKey
                .build();
    }

    @Bean
    public Queue queueC(){
        return QueueBuilder
                .durable(QUEUE_C)
                .deadLetterExchange(Y_DEAD_LETTER_EXCHANGE) //设置死信交换机
                .deadLetterRoutingKey(DEAD_LETTER_ROUTING_KEY) //设置死信RoutingKey
                .build();
    }

    //声明死信队列
    @Bean
    public Queue queueD(){
        return QueueBuilder
                .durable(DEAD_LETTER_QUEUE)
                .build();
    }

    @Bean
    public Binding queueABindingX(){
        return BindingBuilder.bind(queueA()).to(xExchange()).with("XA");
    }

    @Bean
    public Binding queueBBindingX(){
        return BindingBuilder.bind(queueB()).to(xExchange()).with("XB");
    }

    @Bean
    public Binding queueCBindingX(){
        return BindingBuilder.bind(queueC()).to(xExchange()).with("XC");
    }

    @Bean
    public Binding queueDBindingY(){
        return BindingBuilder.bind(queueD()).to(yExchange()).with(DEAD_LETTER_ROUTING_KEY);
    }
}
