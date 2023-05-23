package com.cxf.hotel.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.cxf.hotel.dto.PageResult;
import com.cxf.hotel.dto.RequestParams;
import com.cxf.hotel.mapper.HotelMapper;
import com.cxf.hotel.pojo.Hotel;
import com.cxf.hotel.pojo.HotelDoc;
import com.cxf.hotel.service.IHotelService;
import org.apache.lucene.search.TotalHits;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.geo.GeoPoint;
import org.elasticsearch.common.text.Text;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.SearchHits;
import org.elasticsearch.search.aggregations.Aggregation;
import org.elasticsearch.search.aggregations.AggregationBuilder;
import org.elasticsearch.search.aggregations.AggregationBuilders;
import org.elasticsearch.search.aggregations.Aggregations;
import org.elasticsearch.search.aggregations.bucket.terms.Terms;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.Suggest;
import org.elasticsearch.search.suggest.SuggestBuilder;
import org.elasticsearch.search.suggest.SuggestBuilders;
import org.elasticsearch.search.suggest.completion.CompletionSuggestion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HotelService extends ServiceImpl<HotelMapper, Hotel> implements IHotelService {

    @Autowired
    RestHighLevelClient restHighLevelClient;

    @Override
    public PageResult search(RequestParams requestParams) throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        String location = requestParams.getLocation();
        String key = requestParams.getKey();
        String brand = requestParams.getBrand();
        Integer maxPrice = requestParams.getMaxPrice();
        Integer minPrice = requestParams.getMinPrice();
        String city = requestParams.getCity();
        String starName = requestParams.getStarName();
        String sortBy = requestParams.getSortBy();
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
        if(key == null || "".equals(key)){
            queryBuilder.must(QueryBuilders.matchAllQuery());
        }else {
            queryBuilder.must(QueryBuilders.matchQuery("all",key));
        }
        if(brand != null && !"".equals(brand)){
            queryBuilder.filter(QueryBuilders.termQuery("brand",brand));
        }
        if(city != null && !"".equals(city)){
            queryBuilder.filter(QueryBuilders.termQuery("city",city));
        }
        if(starName != null && !"".equals(starName)){
            queryBuilder.filter(QueryBuilders.termQuery("starName",starName));
        }
        if(location != null && !"".equals(location)){
            request.source().sort(SortBuilders.geoDistanceSort("location",new GeoPoint(location))
                    .order(SortOrder.ASC)
                    .unit(DistanceUnit.KILOMETERS));
        }
        if(minPrice != null && maxPrice != null){
            queryBuilder.filter(QueryBuilders.rangeQuery("price").lte(maxPrice));
            queryBuilder.filter(QueryBuilders.rangeQuery("price").gte(minPrice));
        }

        request.source().query(queryBuilder);
        Integer page = requestParams.getPage();
        Integer size = requestParams.getSize();
        request.source().size(size).from((page - 1) * size);
        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        PageResult pageResult = getPageResult(search);

        return pageResult;
    }

    @Override
    public Map<String, List<String>> filters(RequestParams requestParams) throws IOException {

        String location = requestParams.getLocation();
        String key = requestParams.getKey();
        String brand = requestParams.getBrand();
        Integer maxPrice = requestParams.getMaxPrice();
        Integer minPrice = requestParams.getMinPrice();
        String city = requestParams.getCity();
        String starName = requestParams.getStarName();
        String sortBy = requestParams.getSortBy();
        SearchRequest request = new SearchRequest("hotel");
        BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();

        if(key == null || "".equals(key)){
            queryBuilder.must(QueryBuilders.matchAllQuery());
        }else {
            queryBuilder.must(QueryBuilders.matchQuery("all",key));
        }
        if(brand != null && !"".equals(brand)){
            queryBuilder.filter(QueryBuilders.termQuery("brand",brand));
        }
        if(city != null && !"".equals(city)){
            queryBuilder.filter(QueryBuilders.termQuery("city",city));
        }
        if(starName != null && !"".equals(starName)){
            queryBuilder.filter(QueryBuilders.termQuery("starName",starName));
        }
        if(location != null && !"".equals(location)){
            request.source().sort(SortBuilders.geoDistanceSort("location",new GeoPoint(location))
                    .order(SortOrder.ASC)
                    .unit(DistanceUnit.KILOMETERS));
        }
        if(minPrice != null && maxPrice != null){
            queryBuilder.filter(QueryBuilders.rangeQuery("price").lte(maxPrice));
            queryBuilder.filter(QueryBuilders.rangeQuery("price").gte(minPrice));
        }
        request.source().size(0);
        request.source().aggregation(AggregationBuilders
                .terms("brandAgg")
                .field("brand")
                .size(100))
                .aggregation(AggregationBuilders
                        .terms("cityAgg")
                        .field("city")
                        .size(100))
                .aggregation(AggregationBuilders
                        .terms("starAgg")
                        .field("starName")
                        .size(100))
                .query(queryBuilder);

        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        Map<String, List<String>> map = new HashMap<>();
        Aggregations aggregations = search.getAggregations();

        map.put("品牌",getAggByName(aggregations,"brandAgg"));
        map.put("城市",getAggByName(aggregations,"cityAgg"));
        map.put("星级",getAggByName(aggregations,"starAgg"));

        /*
        Terms brandTerms = aggregations.get("brandAgg");
        List<? extends Terms.Bucket> brandBuckets = brandTerms.getBuckets();
        List<String> bandList = new ArrayList<>();
        brandBuckets.forEach(bucket -> bandList.add(bucket.getKeyAsString()));
        map.put("品牌",bandList);

        Terms cityTerms = aggregations.get("cityAgg");
        List<? extends Terms.Bucket> cityBuckets = cityTerms.getBuckets();
        List<String> cityList = new ArrayList<>();
        cityBuckets.forEach(bucket -> cityList.add(bucket.getKeyAsString()));
        map.put("城市",cityList);

        Terms starTerms = aggregations.get("starAgg");
        List<? extends Terms.Bucket> starBuckets = starTerms.getBuckets();
        List<String> starList = new ArrayList<>();
        starBuckets.forEach(bucket -> starList.add(bucket.getKeyAsString()));
        map.put("星级",starList);

         */

        return map;
    }

    @Override
    public List<String> suggestion(String key) throws IOException {
        SearchRequest request = new SearchRequest("hotel");
        request.source().suggest(new SuggestBuilder()
                .addSuggestion("suggestions", SuggestBuilders
                        .completionSuggestion("suggestion")
                        .prefix(key)
                        .skipDuplicates(true)
                        .size(10)));


        SearchResponse search = restHighLevelClient.search(request, RequestOptions.DEFAULT);
        Suggest suggest = search.getSuggest();
        CompletionSuggestion suggestions = suggest.getSuggestion("suggestions");
        List<CompletionSuggestion.Entry.Option> options = suggestions.getOptions();
        ArrayList<String> list = new ArrayList<>();
        options.forEach(option -> list.add(option.getText().toString()));

        return list;
    }

    private List<String> getAggByName(Aggregations aggregations,String aggregationName){
        Terms terms = aggregations.get(aggregationName);
        List<? extends Terms.Bucket> buckets = terms.getBuckets();
        List<String> list = new ArrayList<>();
        buckets.forEach(bucket -> list.add(bucket.getKeyAsString()));
        return list;
    }

    private PageResult getPageResult(SearchResponse search) {
        PageResult pageResult = new PageResult();
        SearchHits searchHits = search.getHits();
        long value = searchHits.getTotalHits().value;
        pageResult.setTotal(value);
        SearchHit[] hits = searchHits.getHits();
        List<HotelDoc> collect = Arrays.stream(hits).map(hit -> {
            HotelDoc hotelDoc = JSON.parseObject(hit.getSourceAsString(), HotelDoc.class);
            Object[] sortValues = hit.getSortValues();
            if(sortValues!=null){
                hotelDoc.setDistance(sortValues[0]);
            }
            return hotelDoc;
        }).collect(Collectors.toList());
        pageResult.setHotels(collect);
        return pageResult;
    }
}
