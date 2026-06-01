package com.hotel.hotel_server.repository;

import com.hotel.hotel_server.domain.HotelDocument;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

//@Repository
public interface HotelSearchRepository extends ElasticsearchRepository<HotelDocument, String> {

}
