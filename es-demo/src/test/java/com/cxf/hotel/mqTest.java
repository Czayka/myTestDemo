package com.cxf.hotel;

import com.cxf.hotel.constants.MqConstans;
import lombok.AllArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class mqTest {

    @Autowired
    RabbitTemplate rabbitTemplate;

    @Test
    public void testTTL() {
//        Message message = new Message();
        rabbitTemplate.convertAndSend("di.direct",MqConstans.TTL_KEY,"message");
    }
}
