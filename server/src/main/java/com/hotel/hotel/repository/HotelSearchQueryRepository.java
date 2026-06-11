package com.hotel.hotel.repository;

import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import com.hotel.hotel.domain.HotelDocument;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.math.raw.Nat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHitSupport;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.List;

/**
 * [쿼리] : ElasticsearchOperations 사용
 *
 * repository에서 query 빌드
 *      => service는 비즈니스 로직만 담당하도록 책임 분리
 *  es(검색) => id 반환 => querydsl(메인) -가격/인원/예약 가능여부
 */
@Repository
@RequiredArgsConstructor
@Slf4j
public class HotelSearchQueryRepository {
    private final ElasticsearchOperations operations;

    //검색
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

    //자동완성
    public List<String> autocomplete(String q){
        NativeQuery query = NativeQuery.builder()
                .withQuery(qe -> qe
                        .multiMatch(mm-> mm
                                .query(q)
                                .type(TextQueryType.BoolPrefix)
                                .fields("hotelName",
                                        "hotelName.autocomplete",
                                        "hotelName.autocomplete._2gram",
                                        "hotelName.autocomplete._3gram")
                        )
                )
                .withPageable(PageRequest.of(0,8))
                .build();
        return operations.search(query,HotelDocument.class)
                .getSearchHits()
                .stream()
                .map((hit)-> hit.getContent().getHotelName())
                .distinct()
                .toList();
    }

    //최근 본 호텔
    public List<Long> searchSimilar(String lclsSystm2, String lDongRegnCd, String excludeId){
        NativeQuery query = NativeQuery.builder()
                .withQuery(q -> q
                        .bool(b->b
                                .must(m->m.term(
                                        t->t.field("lclsSysm2").value(lclsSystm2)))
                                .must(m->m.term(
                                        t->t.field("lDongRegnCd").value(lDongRegnCd)))
                                .mustNot(mn->mn.term(t->t.field("_id").value(excludeId)))
                        ))
                .withSort(Sort.by(
                        Sort.Order.desc("_score"),
                        Sort.Order.asc("hotelId")
                ))
                .withPageable(PageRequest.of(0, 30))
                .build();

        SearchHits<HotelDocument> hits = operations.search(query, HotelDocument.class);
        List<SearchHit<HotelDocument>> hitList = hits.getSearchHits();


        List<Long> hotelIds = hitList.stream()
                .map(hit -> hit.getContent().getHotelId())
                .toList();

        return hotelIds;
    }
}