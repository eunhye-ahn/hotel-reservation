package com.hotel.payment.mapper;

import com.hotel.admin.dto.payment.AdminSettlementSearchResponse;
import com.hotel.payment.domain.SettlementSearchType;
import com.hotel.payment.domain.SettlementSortType;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SettlementMapper {
    //정산검색조회 데이터
    List<AdminSettlementSearchResponse> searchBySettlement(
            @Param("searchType") SettlementSearchType searchType,
            @Param("keyword") String keyword,
            @Param("hasPendingBalance") Boolean hasPendingBalance,
            @Param("sortType") SettlementSortType sortType,
            @Param("offset") long offset,
            @Param("size") int size
    );

    long countBySettlement(
            @Param("searchType") SettlementSearchType searchType,
            @Param("keyword") String keyword,
            @Param("hasPendingBalance") Boolean hasPendingBalance
    );
}
