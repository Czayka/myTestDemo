package com.cxf.hotel.listen;

import com.cxf.hotel.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class warningListen {
    @RabbitListener(queues = RabbitMQConfig.WARNING_QUEUE_NAME)
    public void receiveWarningMessage(Message message){
        String msg = new String(message.getBody());
        log.info("接收到报警信息：{}",msg);
    }
}
