package com.cxf.hotel.config;

import com.cxf.hotel.constants.MqConstans;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.rabbit.retry.MessageRecoverer;
import org.springframework.amqp.rabbit.retry.RepublishMessageRecoverer;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class RabbitMQConfig implements ApplicationContextAware {

    RabbitTemplate rabbitTemplate = null;

    public static final String CONFIRM_EXCHANGE_NAME = "confirm_exchange";
    public static final String CONFIRM_QUEUE_NAME = "confirm_queue";
    public static final String CONFIRM_EXCHANGE_ROUTING_KEY = "key1";



    //备份交换机
    public static final String BACKUP_EXCHANGE_NAME = "backup_exchange";
    //备份队列
    public static final String BACKUP_QUEUE_NAME = "backup_queue";
    //报警队列
    public static final String WARNING_QUEUE_NAME = "warning_queue";

    @Bean
    public TopicExchange errorExchange(){
        return new TopicExchange(MqConstans.ERROR_EXCHANGE,true,false);
    }
    @Bean
    public Queue errorQueue(){
        return new Queue(MqConstans.ERROR_QUEUE,true);
    }
    @Bean
    public Binding errorQueueBindingErrorExchange(){
        return BindingBuilder.bind(errorQueue()).to(errorExchange()).with(MqConstans.ERROR_ROUTING_KEY);
    }
    @Bean
    public MessageRecoverer messageRecoverer(){
        //AmqpTemplate和RabbitTemplate都可以
        return new RepublishMessageRecoverer(rabbitTemplate, MqConstans.ERROR_EXCHANGE, MqConstans.ERROR_ROUTING_KEY);
    }


    @Bean
    public DirectExchange confirmExchange(){
//        return ExchangeBuilder.directExchange(CONFIRM_EXCHANGE_NAME)
//                .durable(true)
//                .alternate(BACKUP_EXCHANGE_NAME).build();
        return new DirectExchange(CONFIRM_EXCHANGE_NAME,true,false);
    }
    @Bean
    public Queue confirmQueue(){
        return new Queue(CONFIRM_QUEUE_NAME,true);
    }
    @Bean
    public Binding confirmQueueBindingConfirmExchange(){
        return BindingBuilder.bind(confirmQueue()).to(confirmExchange()).with(CONFIRM_EXCHANGE_ROUTING_KEY);
    }

    //备份交换机
    @Bean
    public FanoutExchange backupExchange(){
        return new FanoutExchange(BACKUP_EXCHANGE_NAME,true,false);
    }
    //备份队列
    @Bean
    public Queue backupQueue(){
        return new Queue(BACKUP_QUEUE_NAME,true);
    }
    //报警队列
    @Bean
    public Queue warningQueue(){
        return new Queue(WARNING_QUEUE_NAME,true);
    }
    @Bean
    public Binding backupQueueBindingBackupExchange(){
        return BindingBuilder.bind(backupQueue()).to(backupExchange());
    }
    @Bean
    public Binding warningQueueBindingBackupExchange(){
        return BindingBuilder.bind(warningQueue()).to(backupExchange());
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        // 获取RabbitTemplate
        rabbitTemplate = applicationContext.getBean(RabbitTemplate.class);
        //设置ConfirmCallback
        rabbitTemplate.setConfirmCallback((correlationData, b, s)->{
            String id = correlationData != null ? correlationData.getId() : "";
            if (b){
                log.info("接收到ID为：{}的消息",id);
            }else {
                log.info("未接受到ID为：{}的消息，发生原因：{}",id,s);
                Message returnedMessage = correlationData.getReturnedMessage();
                System.out.println(returnedMessage);
                String s1 = new String(returnedMessage.getBody());
                log.info("s1:{}",s1);
                rabbitTemplate.convertAndSend(RabbitMQConfig.BACKUP_EXCHANGE_NAME,"",s1);
            }
        });
        // 设置ReturnCallback
        rabbitTemplate.setReturnCallback((message, replyCode, replyText, exchange, routingKey) -> {
            // 投递失败，记录日志
            log.info("消息发送失败，应答码{}，原因{}，交换机{}，路由键{},消息{}",
                    replyCode, replyText, exchange, routingKey, message.toString());
            // 如果有业务需要，可以重发消息
            rabbitTemplate.convertAndSend(RabbitMQConfig.BACKUP_EXCHANGE_NAME,"",message);
        });
    }
}
