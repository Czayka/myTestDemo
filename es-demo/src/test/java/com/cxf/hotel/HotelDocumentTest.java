package com.cxf.hotel;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.cxf.hotel.pojo.Hotel;
import com.cxf.hotel.pojo.HotelDoc;
import com.cxf.hotel.service.impl.HotelService;
import com.fasterxml.jackson.core.JsonParser;
import org.apache.lucene.search.BooleanQuery;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.delete.DeleteRequest;
import org.elasticsearch.action.get.GetRequest;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.update.UpdateRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.common.xcontent.XContentType;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.QueryStringQueryBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@SpringBootTest
public class HotelDocumentTest {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Autowired
    HotelService hotelService;

    @Test
    void testAddDocument() throws IOException {
        Hotel hotel = hotelService.getById(61083L);
        HotelDoc hotelDoc = new HotelDoc(hotel);

        IndexRequest request = new IndexRequest("hotel").id(hotelDoc.getId().toString());
        request.source(JSON.toJSONString(hotelDoc), XContentType.JSON);
        restHighLevelClient.index(request, RequestOptions.DEFAULT);
    }

    @Test
    void testGetDocument() throws IOException {
        GetRequest request = new GetRequest("hotel").id("61083");
        GetResponse documentFields = restHighLevelClient.get(request, RequestOptions.DEFAULT);
        String json = documentFields.getSourceAsString();
        HotelDoc hotelDoc = JSON.parseObject(json, HotelDoc.class);
        System.out.println(hotelDoc);
    }

    @Test
    void testUpdateDocument() throws IOException {
        UpdateRequest request = new UpdateRequest("hotel","61083");
        request.doc("name","上海1滴水湖皇冠假日酒店","price",980);
        restHighLevelClient.update(request,RequestOptions.DEFAULT);
    }

    @Test
    void testDeleteDocument() throws IOException {
        DeleteRequest request = new DeleteRequest("hotel").id("61083");
        restHighLevelClient.delete(request,RequestOptions.DEFAULT);
    }

    @Test
    void AddDocument() throws IOException {

        BulkRequest request = new BulkRequest();
        List<Hotel> hotels = hotelService.getBaseMapper().selectList(null);
        hotels.stream().map(hotel -> new HotelDoc(hotel))
                .forEach(hotelDoc -> {
                    request.add(new IndexRequest("hotel")
                            .id(hotelDoc.getId().toString())
                            .source(JSON.toJSONString(hotelDoc),XContentType.JSON));
                });
        restHighLevelClient.bulk(request,RequestOptions.DEFAULT);
    }


}
