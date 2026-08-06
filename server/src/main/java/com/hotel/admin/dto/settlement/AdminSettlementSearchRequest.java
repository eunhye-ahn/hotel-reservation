package com.hotel.admin.dto.settlement;

import com.hotel.payment.domain.SettlementSearchType;
import com.hotel.payment.domain.SettlementSortType;

public record AdminSettlementSearchRequest (
        SettlementSearchType searchType,
        String keyword,
        Boolean hasPendingBalance,
        SettlementSortType sortType,
        int page
){}
