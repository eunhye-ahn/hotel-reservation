package com.hotel.hotel.repository;

import com.hotel.hotel.domain.HotelDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

//@Repository
public interface HotelSearchRepository extends ElasticsearchRepository<HotelDocument, String> {

}
