package com.cxf.hotel.listen;

import com.cxf.hotel.config.RabbitMQConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class confirmListen {
    @RabbitListener(queues = RabbitMQConfig.CONFIRM_QUEUE_NAME)
    public void receiveConfirmMessage(Message message){
        String msg = new String(message.getBody());
        log.info("接受到队列confirm.queue消息：{}",msg);
    }
}
