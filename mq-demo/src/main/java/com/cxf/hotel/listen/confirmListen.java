package com.cxf.hotel.listen;

import com.cxf.hotel.config.RabbitMQConfig;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@Slf4j
public class confirmListen {
    @RabbitListener(queues = RabbitMQConfig.CONFIRM_QUEUE_NAME)
    public void receiveConfirmMessage(Message message, Channel channel) throws IOException {

//        String messageId = message.getMessageProperties().getMessageId();
//        log.info("message:{},messgeId:{}",message,messageId);
//        RLock lock = redissonClient.getLock("confirm_mq:" + messageId);
//        if (lock.tryLock()){
//            try {
//                String msg = new String(message.getBody());
//                log.info("接收到消息：{}，开始处理消息",msg);
//                //开始处理消息
//
//            }finally {
//                lock.lock();
//            }
//        }else {
//            channel.basicReject(message.getMessageProperties().getDeliveryTag(), false);
//        }

        String msg = new String(message.getBody());
        log.info("接受到队列confirm.queue消息：{}",msg);
    }
}
