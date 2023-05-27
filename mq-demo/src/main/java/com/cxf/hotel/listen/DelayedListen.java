package com.cxf.hotel.listen;

import com.cxf.hotel.constants.MqConstans;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Slf4j
public class DelayedListen {

    @RabbitListener(queues = MqConstans.DELAYED_QUEUE)
    public void receiveDelayedMessage(Message message){
        String msg = new String(message.getBody());
        log.info("当前时间：{}，收到延迟队列的信息：{}",new Date().toString(),msg);
    }
}
