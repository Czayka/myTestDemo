package com.cxf.hotel;

import com.alibaba.fastjson.JSON;
import com.cxf.hotel.service.impl.HotelService;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.elasticsearch.search.suggest.completion.CompletionSuggestionBuilder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class HotelBucketTest {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Autowired
    HotelService hotelService;

    @Test
    void load() throws IOException {
//        Map<String, List<String>> filters = hotelService.filters();
//        System.out.println(filters);
    }

    @Test
    void testSuggestion() throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source().suggest(new SuggestBuilder()
                .addSuggestion("suggestions",SuggestBuilders
                        .completionSuggestion("suggestion")
                        .prefix("sh")
                        .skipDuplicates(true)
                        .size(10)));


        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        Suggest suggest = search.getSuggest();
        CompletionSuggestion suggestions = suggest.getSuggestion("suggestions");
        List<CompletionSuggestion.Entry.Option> options = suggestions.getOptions();
        options.forEach(
                option -> {
                    Text text = option.getText();
                    System.out.println(text);
                }
        );

//        System.out.println(search);
    }

    @Test
    void testBucket() throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source().size(0);
        request.source().aggregation(AggregationBuilders
                .terms("brand")
                .field("brand")
                .size(10));
        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        extracted(search);
    }

    private void extracted(SearchResponse search) {
        Aggregations aggregations = search.getAggregations();
        Terms terms = aggregations.get("brand");
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        buckets.forEach(
                bucket -> {
                    String keyAsString = bucket.getKeyAsString();
                    long docCount = bucket.getDocCount();
                    System.out.println(keyAsString+"    "+docCount);
                }
        );
    }
}
