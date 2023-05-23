package com.cxf.hotel.dto;

import com.cxf.hotel.pojo.HotelDoc;
import lombok.Data;

import java.util.List;

@Data
public class PageResult {
    private Long total;
    private List<HotelDoc> hotels;
}
