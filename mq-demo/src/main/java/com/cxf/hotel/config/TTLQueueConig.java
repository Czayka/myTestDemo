package com.cxf.hotel.config;

import com.cxf.hotel.constants.MqConstans;
import com.rabbitmq.client.AMQP;
import org.springframework.amqp.core.*;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

@Configuration
public class TTLQueueConig {
    /*
    //普通交换机名称
    public static final String MY_EXCHANGE = "MY";
    //死信交换机名称
    public static final String MY_DEAD_LETTER_EXCHANGE = "MY_DEAD_LETTER";
    //普通队列名称
    public static final String QUEUE_MY = "Q_MY";
    //死信队列名称
    public static final String DEAD_LETTER_QUEUE_MY = "DEAD_LETTER_Q_MY";
    //普通RoutingKey名称
    public static final String MY_ROUTING_KEY = "ROUTING_MY";
    //死信RoutingKey名称
    public static final String DEAD_LETTER_ROUTING_KEY = "DEAD_LETTER_ROUTING_MY";
    */

    //声明普通交换机
    @Bean
    public TopicExchange myExchange(){
        return new TopicExchange(MqConstans.MY_EXCHANGE,true,false);
    }

    //声明死信交换机
    @Bean
    public TopicExchange myDeadLetterExchange(){
        return new TopicExchange(MqConstans.MY_DEAD_LETTER_EXCHANGE,true,false);
    }

    //声明普通队列
    @Bean
    public Queue myQueue(){
        return QueueBuilder
                .durable(MqConstans.QUEUE_MY)
                .deadLetterExchange(MqConstans.MY_DEAD_LETTER_EXCHANGE) //设置死信交换机
                .deadLetterRoutingKey(MqConstans.DEAD_LETTER_ROUTING_KEY) //设置死信RoutingKey
                .build();
    }

    //声明死信队列
    @Bean
    public Queue myDeadLetterQueue(){
        return QueueBuilder
                .durable(MqConstans.DEAD_LETTER_QUEUE_MY)
                .build();
    }

    //绑定普通队列
    @Bean
    public Binding myQueueBindingMyExchange(){
        return BindingBuilder.bind(myQueue()).to(myExchange()).with(MqConstans.MY_ROUTING_KEY);
    }

    //绑定死信队列
    @Bean
    public Binding myDeadLetterQueueBindingMyDeadLetterExchange(){
        return BindingBuilder.bind(myDeadLetterQueue()).to(myDeadLetterExchange()).with(MqConstans.DEAD_LETTER_ROUTING_KEY);
    }


}
