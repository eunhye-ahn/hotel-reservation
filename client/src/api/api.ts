import type { AccessTokenResponse, LoginRequest, SignUpRequest } from "@/api/types/auth";
import axios from "axios";
import type { AddWishListResponse, AdminInventorySummaryResponse, HotelDetailResponse, HotelListFilter, hotelResponse, MoveWishRequest, MoveWishResponse, Page, searchInventorySummaryRequest, SettlementHistoryResponse, WishCollectionsRequest, WishListCollectionResponse } from "@/api/types/hotel";
import type { ReservationCreateResponse, ReservationDetailResponse, ReservationInfoResponse, ReservationRequest, ReservationResponse } from "@/api/types/reservation";
import type { UserInfoResponse } from "./types/user";
import { api } from "./axios";
import type { PaymentConfirmRequest, PaymentConfirmResponse, PaymentPrepareResponse } from "@/api/types/payment";
import type { AdminPaymentResponse, AdminPaymentSearchRequest, AdminReseervationSearchRequest, AdminReservationDetailResponse, AdminReservationSearchResponse, AdminRoomInfoResponse, AdminRoomResponse, AdminSettlementSearchRequest, AdminSettlementSearchResponse, AssignmentRoomRequest, CancelReservationByAdminRequest, DailyStatisticsResponse, DashBoardSummaryResponse, PaymentStatusStaticResponse, ReserveStatusStaticResponse, RoomFilterOptionResponse, RoomTypeInventoryCalendarResponse, searchRoomInfoRequest, SettlementHistorySearchRequest, TopPendingBalanceHotel, UnassignRoomInfo } from "@/api/types/admin";

export const login = (request: LoginRequest) => {
    return api.post<AccessTokenResponse>("/auth/login", request);
}

export const logout = () => {
    return api.post<void>("/auth/logout");
}

//새로고침,at만료 시(401반환 시 -인증실패) 호출 - 무한루프 방지
export const reissue = () => {
    return axios.post<AccessTokenResponse>(`${import.meta.env.VITE_API_BASE_URL}/auth/reissue`, null, {
        withCredentials: true
    });
}

export const signUp = (request: SignUpRequest) => {
    return api.post<AccessTokenResponse>("/auth/signUp", request);
}


export const getPopularHotel = () => {
    return api.get<hotelResponse[]>("/hotels")
}

//호텔필터조회
export const getHotelsByFilter = (filter: HotelListFilter, page: number = 0) => {
    return api.get<Page<hotelResponse>>("/hotels/filter", {
        params: {
            q: filter.q || undefined,
            lDongRegnCd: filter.regionCode,
            lDongSignguCd: filter.subRegionCode,
            lclsSystm2: filter.lclsSystm2,
            startDate: filter.checkIn,
            endDate: filter.checkOut,
            numberOfGuests: filter.numberOfGuests,
            numberOfRooms: filter.numberOfRooms,
            page
        }
    });
}

export const getHotelDetail = (hotelId: number, startDate?: string, endDate?: string, numberOfRooms?: number, numberOfGuests?: number) => {
    return api.get<HotelDetailResponse>(`/hotels/${hotelId}`, {
        params: { startDate, endDate, numberOfRooms, numberOfGuests }
    });
}

export const createReservation = (request: ReservationRequest, reservationKey: string) => {
    return api.post<ReservationCreateResponse>("/reservations", request, {
        headers: { "Idempotency-Key": reservationKey }
    })
}

export const reservationConfirm = (reservationKey: string) => {
    return api.get<ReservationDetailResponse>(`/reservations/${reservationKey}`)
}

export const getMyInfo = () => {
    return api.get<UserInfoResponse>("/user/myInfo")
}

export const getMyReservations = (status: string, page: number) => {
    return api.get<Page<ReservationResponse>>("/reservations", {
        params: { status, page }
    })
}

export const cancelReservation = (reservationKey: string) => {
    return api.delete<void>(`/reservations/${reservationKey}`)
}

export const reservationInfo = (reservationKey: string) => {
    return api.get<ReservationInfoResponse>(`/reservations/${reservationKey}/reservation-info`)
}

export const getReservationStatus = (reservationKey: string) => {
    return api.get<string>(`/reservations/${reservationKey}/status`)
}

export const getSearchAutocomplete = (q?: string) => {
    return api.get<string[]>("/hotels/autocomplete", {
        params: { q }
    })
}

export const getSimilarHotel = (hotelId: number) => {
    return api.get<hotelResponse[]>("/hotels/similarHotel", {
        params: { hotelId }
    })
}

export const createCollection = (request: WishCollectionsRequest) => {
    return api.post<void>("/wish/collection", request)
}

export const getCollections = () => {
    return api.get<WishListCollectionResponse[]>("/wish/collection/all")
}

export const getCollection = (collectionId: number) => {
    return api.get<WishListCollectionResponse>("/wish/collection", {
        params: { collectionId }
    })
}

export const addWishList = (hotelId: number) => {
    return api.post<AddWishListResponse>("/wish/list", null, {
        params: { hotelId }
    })
}

export const moveCollection = (request: MoveWishRequest) => {
    return api.post<MoveWishResponse>("/wish/move", request)
}

export const cancelWishList = (hotelId: number) => {
    return api.delete<void>("/wish/cancel", {
        params: { hotelId }
    })
}

export const getWishedChecked = (hotelId: number) => {
    return api.get<boolean>("/wish/check", {
        params: { hotelId }
    })
}

export const preparePayment = (reservationKey: string, orderId: string, paymentKey: string) => {
    return api.post<PaymentPrepareResponse>(`/payments/prepare/${reservationKey}`, { orderId }, {
        headers: {
            "Idempotency-Key": paymentKey
        }
    })
}

//결제승인처리api
export const confirmPayment = (request: PaymentConfirmRequest) => {
    return api.post<PaymentConfirmResponse>("/payments/confirm", request)
}


export const deleteCollection = (collectionId: number) => {
    return api.delete<void>("/wish/collection/delete", {
        params: { collectionId }
    })
}


//============관리자

//예약 조회(필터)
export const getReservations = (params: AdminReseervationSearchRequest) => {
    return api.get<Page<AdminReservationSearchResponse>>("/admin/reservation", {
        params
    })
}

//예약 상세
export const getReservationDetail = (reservationId: number) => {
    return api.get<AdminReservationDetailResponse>(`/admin/reservation/${reservationId}`)
}

export const getRoomsByReservation = (reservationId: number) => {
    return api.get<AdminRoomResponse[]>(`admin/reservation/${reservationId}/rooms`)
}

//객실 배정
export const assignRoom = (reservationId: number, roomId: number) => {
    return api.patch<void>(`admin/reservation/${reservationId}/assign-rooms`, { roomId } as AssignmentRoomRequest)
}

//객실 배정취소
export const unassignRoom = (reservationId: number) => {
    return api.patch<void>(`admin/reservation/${reservationId}/unassign-rooms`)
}

//예약취소 - 관리자
export const cancelReservationByAdmin = (reservationId: number, cancelReason: string) => {
    return api.post<void>(`admin/reservation/${reservationId}/cancel`, { cancelReason } as CancelReservationByAdminRequest)
}

//결제내역 조회
export const getPayments = (params: AdminPaymentSearchRequest) => {
    return api.get<Page<AdminPaymentResponse>>("admin/payment", {
        params
    })
}

//정산내역 조회
export const getSettlements = (params: AdminSettlementSearchRequest) => {
    return api.get<Page<AdminSettlementSearchResponse>>("admin/settlement", {
        params
    })
}

//기간내 정산액 조회
export const previewSettlementAmount = (hotelId: number, periodStart?: string, periodEnd?: string) => {
    return api.get<number>(`admin/settlement/${hotelId}/preview`, {
        params: { periodStart, periodEnd }
    })
}

//수동정산
export const executeSettlementByAdmin = (hotelId: number, settlementKey: string, periodStart?: string, periodEnd?: string) => {
    return api.post<void>(`admin/settlement/${hotelId}/execute`,
        { periodStart, periodEnd },
        { headers: { "Idempotency-Key": settlementKey } }
    )
}

//특정 호텔 정산내역 조회
export const getSettlementByHotel = (hotelId: number, params: SettlementHistorySearchRequest) => {
    return api.get<Page<SettlementHistoryResponse>>(`/admin/settlement/${hotelId}/list`, {
        params
    })
}

//호텔 재고조회
export const searchInventorySummary = (params: searchInventorySummaryRequest) => {
    return api.get<Page<AdminInventorySummaryResponse>>("/admin/inventory", {
        params
    })
}

//호텔 상세재고조회 - 캘린더
export const getInventoryCalendar = (hotelId: number, startDate: string, endDate: string) => {
    return api.get<RoomTypeInventoryCalendarResponse[]>(`/admin/inventory/${hotelId}`, {
        params: { startDate, endDate }
    })
}

//호텔 상세재고조회 - 객실
export const searchByRoomInfo = (hotelId: number, params: searchRoomInfoRequest) => {
    return api.get<Page<AdminRoomInfoResponse>>(`/admin/inventory/${hotelId}/room`, {
        params
    })
}

export const getFilterOptions = (hotelId: number) => {
    return api.get<RoomFilterOptionResponse>(`/admin/inventory/${hotelId}/room/filter-option`)
}

export const getDashBoardSummaryInfo = () => {
    return api.get<DashBoardSummaryResponse>("/admin/dashboard/summary")
}

export const getDailyStatisticsInfo = () => {
    return api.get<DailyStatisticsResponse[]>("/admin/dashboard/dailyStatistics")
}

export const getUnAssignReservationInfo = () => {
    return api.get<UnassignRoomInfo[]>("/admin/dashboard/unAssign-reserve")
}

export const getReserveStatusByMonth = () => {
    return api.get<ReserveStatusStaticResponse[]>("/admin/dashboard/reserve-status/statiscs")
}

export const getPaymentStatusByMonth = () => {
    return api.get<PaymentStatusStaticResponse[]>("/admin/dashboard/payment-status/statics")
}

export const getTopPendingHotels = () => {
    return api.get<TopPendingBalanceHotel[]>("/admin/dashboard/top-pending/hotels")
}
