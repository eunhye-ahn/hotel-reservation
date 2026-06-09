package com.hotel.hotel.repository;

import com.hotel.hotel.domain.HotelDocument;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * repository에서 query 빌드
 *      => service는 비즈니스 로직만 담당하도록 책임 분리
 *  es(검색) => id 반환 => querydsl(메인) -가격/인원/예약 가능여부
 */
@Repository
@RequiredArgsConstructor
public class HotelSearchQueryRepository {
    private final ElasticsearchOperations operations;

    public List<Long> search(String q) {
        NativeQuery query = NativeQuery.builder()
                .withQuery(qe -> qe
                        .bool(b -> {
                            if (StringUtils.hasText(q)) {
                                b.must(m -> m
                                        .multiMatch(mm -> mm
                                                .query(q)
                                                .fields("hotelName^3", "address^2")));
                            }
                            if (!StringUtils.hasText(q)) {
                                b.must(m -> m.matchAll(ma -> ma));
                            }
                            return b;
                        }))
                .withSort(Sort.by(
                        Sort.Order.desc("_score"),
                        Sort.Order.asc("hotelId")
                ))
                .withPageable(PageRequest.of(0, 1000))
                .build();

        SearchHits<HotelDocument> hits = operations.search(query, HotelDocument.class);
        List<SearchHit<HotelDocument>> hitList = hits.getSearchHits();


        List<Long> hotelIds = hitList.stream()
                .map(hit -> hit.getContent().getHotelId())
                .toList();

        return hotelIds;
    }
}