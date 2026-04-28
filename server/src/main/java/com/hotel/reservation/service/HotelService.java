package com.hotel.reservation.service;

import com.hotel.reservation.dto.HotelResponse;
import com.hotel.reservation.repository.HotelRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.query.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.awt.print.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HotelService {
    private final int PAGE_SIZE = 21;
    private final HotelRepository hotelRepository;

    //호텔조회
    public Page<HotelResponse> getHotels(int page){
        Pageable pageable = PageRequest.of(page,PAGE_SIZE);
        return hotelRepository.findAll(pageable)
                .map(HotelResponse::from);

    }
}
