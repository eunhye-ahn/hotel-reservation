import type { AccessTokenResponse, LoginRequest, SignUpRequest } from "@/type/auth";
import axios from "axios";
import type { AddWishListRequest, AddWishListResponse, CursorResponse, HotelDetailResponse, hotelResponse, MoveWishRequest, MoveWishResponse, Page, WishCollectionsRequest, WishListCollectionResponse } from "@/type/hotel";
import type { ReservationCreateResponse, ReservationDetailResponse, ReservationInfoResponse, ReservationRequest, ReservationResponse, RoomTypeReservationResponse } from "@/type/reservation";
import type { UserInfoResponse } from "@/type/user";
import { api } from "./axios";
import type { PaymentConfirmRequest, PaymentConfirmResponse, PaymentPrepareResponse } from "@/type/payment";
import type { AdminReseervationSearchRequest, AdminReservationDetailResponse, AdminReservationSearchResponse, AdminRoomListResponse, AssignmentRoomRequest } from "@/type/admin";

export const login = (request: LoginRequest) => {
    return api.post<AccessTokenResponse>("/auth/login", request);
}

export const logout = () => {
    return api.post<void>("/auth/logout");
}

//새로고침,at만료 시(401반환 시 -인증실패) 호출 - 무한루프 방지
export const reissue = () => {
    return axios.post<AccessTokenResponse>("http://localhost:8080/api/v1/auth/reissue", null, {
        withCredentials: true
    });
}

export const signUp = (request: SignUpRequest) => {
    return api.post<AccessTokenResponse>("/auth/signUp", request);
}

//호텔전체조회
export const getHotels = (cursorId?: number) => {
    return api.get<CursorResponse>("/hotels", {
        params: { cursorId }
    })
}

/**
 * 
 * 
// 순서 기반 - q 자리 undefined로 채워야 함
getHotelsByFilter(undefined, regionCode, ...)

// 객체 기반 - q 그냥 생략
getHotelsByFilter({ lDongRegnCd: regionCode, ... })

=> 객체기반으로 변경작업필요
 */
//호텔필터조회
export const getHotelsByFilter = (q?: string, lDongRegnCd?: string, lDongSignguCd?: string, lclsSystm2?: string,
    startDate?: string, endDate?: string, numberOfGuests?: number, numberOfRooms?: number, cursorId: number = 0) => {
    return api.get<CursorResponse>("/hotels", {
        params: {
            q,
            lDongRegnCd, lDongSignguCd, lclsSystm2,
            startDate,
            endDate,
            numberOfGuests,
            numberOfRooms,
            cursorId
        }
    });
}

export const getHotelDetail = (hotelId: number, startDate?: string, endDate?: string, numberOfRooms?: number, numberOfGuests?: number) => {
    return api.get<HotelDetailResponse>(`/hotels/${hotelId}`, {
        params: { startDate, endDate, numberOfRooms, numberOfGuests }
    });
}

export const createReservation = (request: ReservationRequest) => {
    return api.post<ReservationCreateResponse>("/reservations", request)
}

export const getRoomTypeForReservation = (hotelId: number, roomTypeId: number, startDate: string, endDate: string, numberOfRooms: number) => {
    return api.get<RoomTypeReservationResponse>(`/hotels/${hotelId}/roomTypes/${roomTypeId}/reservation`, {
        params: { startDate, endDate, numberOfRooms }
    })
}

export const reservationConfirm = (reservationKey: string) => {
    return api.get<ReservationDetailResponse>(`/reservations/${reservationKey}`)
}

export const getMyInfo = () => {
    return api.get<UserInfoResponse>("/user/myInfo")
}

export const getMyReservations = (status: string) => {
    return api.get<ReservationResponse[]>("/reservations", {
        params: { status }
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

export const getSimilarHotel = (hotelId: number, page: number) => {
    return api.get<Page<hotelResponse>>("/hotels/similarHotel", {
        params: { hotelId, page }
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

export const preparePayment = (reservationKey: string, orderId: string, idempotencyKey: string) => {
    return api.post<PaymentPrepareResponse>(`/payments/prepare/${reservationKey}`, { orderId }, {
        headers: {
            "Idempotency-Key": idempotencyKey
        }
    })
}

//결제승인처리api
export const confirmPayment = (request: PaymentConfirmRequest) => {
    return api.post<PaymentConfirmResponse>("/payments/confirm", request)
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
    return api.get<AdminRoomListResponse[]>(`admin/reservation/${reservationId}/rooms`)
}

//객실 배정
export const assignRoom = (reservationId: number, roomId: number) => {
    return api.post<void>(`admin/reservation/${reservationId}/assign-rooms`, { roomId } as AssignmentRoomRequest)
}