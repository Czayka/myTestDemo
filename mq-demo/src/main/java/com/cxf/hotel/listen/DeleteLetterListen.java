package com.cxf.hotel.listen;

import com.cxf.hotel.constants.MqConstans;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class DeleteLetterListen {

    //接收消息
    @RabbitListener(queues = MqConstans.DEAD_LETTER_QUEUE_MY)
    public void receiveD(Message message, Channel channel) throws Exception{
        String msg = new String(message.getBody());
        log.info("当前时间：{}，接受到死信队列消息：{}",new Date().toString(),msg);
    }
}
