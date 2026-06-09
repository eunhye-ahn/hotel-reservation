package com.hotel.hotel.repository;

import com.hotel.hotel.domain.HotelDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

/**
 * [인덱스 데이터 저장/삭제] : ElasticsearchRepository 상속
 */
@Repository
public interface HotelSearchRepository extends ElasticsearchRepository<HotelDocument, String> {

}
