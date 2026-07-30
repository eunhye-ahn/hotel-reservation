package com.hotel.admin.service;

import com.hotel.admin.dto.payment.AdminSettlementSearchResponse;
import com.hotel.admin.dto.settlement.SettlementHistoryResponse;
import com.hotel.common.exception.CustomException;
import com.hotel.common.exception.ErrorCode;
import com.hotel.hotel.domain.Hotel;
import com.hotel.hotel.repository.HotelRepository;
import com.hotel.payment.domain.*;
import com.hotel.payment.mapper.SettlementMapper;
import com.hotel.payment.repository.LedgerRepository;
import com.hotel.payment.repository.SettlementRepository;
import com.hotel.payment.service.SettlementService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AdminSettlementService {
    private final SettlementService settlementService;
    private final HotelRepository hotelRepository;
    private final LedgerRepository ledgerRepository;
    private final SettlementMapper mapper;
    private final SettlementRepository settlementRepository;

    public void executeSettlementByAdmin(Long hotelId, LocalDate periodStart, LocalDate periodEnd){
        settlementService.executeSettlementByAdmin(hotelId, periodStart, periodEnd);
    }

    //정산내역 조회
    public Page<AdminSettlementSearchResponse> getSettlements(
            SettlementSearchType searchType, String keyword, Boolean hasPendingBalance, SettlementSortType sortType, Pageable pageable
    ){
        List<AdminSettlementSearchResponse> content = mapper.searchBySettlement(
                searchType, keyword, hasPendingBalance, sortType, pageable.getOffset(), pageable.getPageSize()
        );
        long total = mapper.countBySettlement(searchType, keyword, hasPendingBalance);

        return new PageImpl<>(content, pageable, total);
    }

    public int previewSettlementAmount(Long hotelId, LocalDate periodStart, LocalDate periodEnd){
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(()->new CustomException(ErrorCode.HOTEL_NOT_FOUND));

        //지갑뒤지기
        return ledgerRepository.calculateSettlementAmount(
                hotel.getSellerAccount(),
                AccountType.SELLER,
                periodStart.atTime(0,0,0),
                periodEnd.atTime(23,59,59)
        );
    }

    //호텔 정산이력 조회
    public Page<SettlementHistoryResponse> getSettlementByHotel(Long hotelId,
                                                                LocalDate startDate,
                                                                LocalDate endDate,
                                                                SettlementStatus status,

                                                                Pageable pageable){
        Hotel hotel = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new CustomException(ErrorCode.HOTEL_NOT_FOUND));
        return settlementRepository.searchSettlementByHotel(hotel.getSellerAccount(), startDate, endDate, status, pageable);
    }
}
