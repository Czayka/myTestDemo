package com.cxf.hotel;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.ISqlSegment;
import com.cxf.hotel.service.impl.HotelService;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.lucene.search.function.FunctionScoreQuery;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightField;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@SpringBootTest
public class HotelSearchTest {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Autowired
    HotelService hotelService;

    @Test
    void testMatchAll() throws IOException {
        SearchRequest request = new SearchRequest("hotel");

        request.source().query(QueryBuilders.matchAllQuery()).from(10).size(20);
        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        extracted(search);
//        System.out.println(search.toString());
    }
    @Test
    void testBool() throws IOException {
        SearchRequest request = new SearchRequest("hotel");

        request.source().query(QueryBuilders.boolQuery()
                        .must(QueryBuilders.matchQuery("all","如家"))
                        .must(QueryBuilders.termQuery("city","上海"))
                        .filter(QueryBuilders.rangeQuery("price").lte(250)))
                .highlighter(new HighlightBuilder().field("name").requireFieldMatch(false))//高亮
                .sort("price");//排序
                //.from(10).size(10);//分页
        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        SearchHits searchHits = search.getHits();
        long value = searchHits.getTotalHits().value;
        System.out.println("查询到了"+value+"条数据");
        SearchHit[] hits = searchHits.getHits();
        for (SearchHit hit : hits) {
            String sourceAsString = hit.getSourceAsString();
            Map<String, HighlightField> highlightFields = hit.getHighlightFields();
            HighlightField name = highlightFields.get("name");
            System.out.println(name);
            String s = JSON.parse(sourceAsString).toString();
            System.out.println(s);
        }

    }

    @Test
    void testMultiMatch() throws IOException {
        SearchRequest request = new SearchRequest("hotel");

        request.source().query(QueryBuilders.multiMatchQuery("如家","all")).from(10).size(10);
        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        extracted(search);
    }

    private void extracted(SearchResponse search) {
        SearchHits hits = search.getHits();
        long value = hits.getTotalHits().value;
        System.out.println("查询到了"+value+"条数据");
        SearchHit[] hits1 = hits.getHits();
        Arrays.stream(hits1).forEach(documentFields -> {
            String sourceAsString = documentFields.getSourceAsString();
            String s = JSON.parse(sourceAsString).toString();
            System.out.println(s);
        });
    }

}
