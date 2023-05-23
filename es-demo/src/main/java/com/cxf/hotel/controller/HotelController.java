package com.cxf.hotel.controller;

import com.cxf.hotel.dto.PageResult;
import com.cxf.hotel.dto.RequestParams;
import com.cxf.hotel.service.impl.HotelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/hotel")
public class HotelController {

    @Autowired
    HotelService hotelService;

    @PostMapping("/list")
    public PageResult search(@RequestBody RequestParams requestParams) throws IOException {
        return hotelService.search(requestParams);
    }
    @PostMapping("/filters")
    public Map<String, List<String>> filters(@RequestBody RequestParams requestParams) throws IOException {
        return hotelService.filters(requestParams);
    }
    @GetMapping("/suggestion")
    public List<String> suggestion (@RequestParam("key") String key) throws IOException {
        return hotelService.suggestion(key);
    }
}
