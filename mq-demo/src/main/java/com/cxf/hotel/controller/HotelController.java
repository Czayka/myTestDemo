package com.cxf.hotel.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.cxf.hotel.constants.MqConstans;
import com.cxf.hotel.pojo.Hotel;
import com.cxf.hotel.pojo.PageResult;
import com.cxf.hotel.service.IHotelService;
import com.cxf.hotel.service.impl.HotelService;
import org.springframework.amqp.core.MessageBuilder;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.security.InvalidParameterException;

@RestController
@RequestMapping("hotel")
public class HotelController {

    @Autowired
    private HotelService hotelService;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @GetMapping("/{id}")
    public Hotel queryById(@PathVariable("id") Long id){
        Hotel byId = hotelService.getById(id);
        return byId;
    }

    @GetMapping("/list")
    public PageResult hotelList(
            @RequestParam(value = "page", defaultValue = "1") Integer page,
            @RequestParam(value = "size", defaultValue = "1") Integer size
    ){
        Page<Hotel> result = hotelService.page(new Page<>(page, size));

        return new PageResult(result.getTotal(), result.getRecords());
    }

    @PostMapping
    public void saveHotel(@RequestBody Hotel hotel){
        hotelService.save(hotel);

        rabbitTemplate.convertAndSend(MqConstans.HOTEL_EXCHANGE,MqConstans.HOTEL_INSERT_KEY,hotel.getId());
    }

    @PutMapping()
    public void updateById(@RequestBody Hotel hotel){
        if (hotel.getId() == null) {
            throw new InvalidParameterException("id不能为空");
        }
        hotelService.updateById(hotel);
        rabbitTemplate.convertAndSend(MqConstans.HOTEL_EXCHANGE,MqConstans.HOTEL_INSERT_KEY,hotel.getId());
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        hotelService.removeById(id);
        rabbitTemplate.convertAndSend(MqConstans.HOTEL_EXCHANGE,MqConstans.HOTEL_DELETE_KEY,id);
    }
}
