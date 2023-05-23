package com.cxf.hotel;

import net.sf.jsqlparser.statement.create.index.CreateIndex;
import org.apache.http.HttpHost;
import org.elasticsearch.action.admin.indices.delete.DeleteIndexRequest;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.client.indices.CreateIndexRequest;
import org.elasticsearch.client.indices.GetIndexRequest;
import org.elasticsearch.common.xcontent.XContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import com.cxf.hotel.constants.HotelConstants;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;


public class HotelTest {


    private RestHighLevelClient restHighLevelClient;

    @BeforeEach
    void setUp(){
        this.restHighLevelClient = new RestHighLevelClient(
                RestClient.builder(
                        HttpHost.create("http://192.168.3.6:9200")
                )
        );
    }

    @AfterEach
    void tearDown() throws IOException {
        this.restHighLevelClient.close();
    }

    @Test
    void testInit(){
        System.out.println(restHighLevelClient);
    }

    @Test
    void testCreateHotelIndex() throws IOException {
        CreateIndexRequest request = new CreateIndexRequest("hotel");
        request.source(HotelConstants.MAPPING_TEMPLATE, XContentType.JSON);
        restHighLevelClient.indices().create(request, RequestOptions.DEFAULT);
    }

    @Test
    void testDELETEHotelIndex() throws IOException {
        DeleteIndexRequest request = new DeleteIndexRequest("hotel");
        restHighLevelClient.indices().delete(request, RequestOptions.DEFAULT);
    }

    @Test
    void testHotelIndexExists() throws IOException {
        GetIndexRequest request = new GetIndexRequest("hotel");
        boolean exists = restHighLevelClient.indices().exists(request, RequestOptions.DEFAULT);
        System.out.println(exists);
    }
}
