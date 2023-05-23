package com.cxf.hotel.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.cxf.hotel.dto.PageResult;
import com.cxf.hotel.dto.RequestParams;
import com.cxf.hotel.pojo.Hotel;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface IHotelService extends IService<Hotel> {
    public PageResult search(RequestParams requestParams) throws IOException;
    Map<String, List<String>> filters(RequestParams requestParams) throws IOException;

    List<String> suggestion(String key) throws IOException;
}
