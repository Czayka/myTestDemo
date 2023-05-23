package com.cxf.hotel.mq;

import com.alibaba.fastjson.JSON;
import com.cxf.hotel.constants.MqConstans;
import com.cxf.hotel.pojo.Hotel;
import com.cxf.hotel.pojo.HotelDoc;
import com.cxf.hotel.service.impl.HotelService;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class HotelListener {

    @Autowired
    private HotelService hotelService;

    @Autowired
    RestHighLevelClient restHighLevelClient;

    /**
     * 监听新增或修改
     * @param id
     */
    @RabbitListener(queues = MqConstans.HOTEL_INSERT_QUEUE)
    public void listenHotelInsertOrUpdate(Long id) throws IOException {
        Hotel hotel = hotelService.getById(id);
        HotelDoc hotelDoc = new HotelDoc(hotel);

        IndexRequest request = new IndexRequest("hotel").id(hotelDoc.getId().toString());
        request.source(JSON.toJSONString(hotelDoc), XContentType.JSON);
        restHighLevelClient.index(request, RequestOptions.DEFAULT);
    }

    /**
     * 监听删除
     * @param id
     */
    @RabbitListener(queues = MqConstans.HOTEL_DELETE_QUEUE)
    public void listenHotelDelete(Long id) throws IOException {

        DeleteRequest request = new DeleteRequest("hotel").id(id.toString());
        restHighLevelClient.delete(request,RequestOptions.DEFAULT);
    }
}
