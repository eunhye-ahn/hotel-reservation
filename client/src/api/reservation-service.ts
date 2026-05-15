import type { AccessTokenResponse, LoginRequest, SignUpRequest } from "@/shared/type/auth";
import axios from "axios";
import type { HotelDetailResponse, hotelResponse, Page } from "@/shared/type/hotel";
import type { ReservationDetailResponse, ReservationInfoResponse, ReservationRequest, ReservationResponse, RoomTypeReservationResponse } from "@/shared/type/reservation";
import type { UserInfoResponse } from "@/shared/type/user";
import { reservationApi } from "./axios/reservation-axios";

export const login = (request: LoginRequest) => {
    return reservationApi.post<AccessTokenResponse>("/auth/login", request);
}

export const logout = () => {
    return reservationApi.post<void>("/auth/logout");
}

//새로고침,at만료 시(401반환 시 -인증실패) 호출 - 무한루프 방지
export const reissue = () => {
    return axios.post<AccessTokenResponse>("http://localhost:8080/api/v1/auth/reissue", null, {
        withCredentials: true
    });
}

export const signUp = (request: SignUpRequest) => {
    return reservationApi.post<AccessTokenResponse>("/auth/signUp", request);
}

export const getHotels = () => {
    return reservationApi.get<Page<hotelResponse>>("/hotels");
}

export const getHotelDetail = (hotelId: number) => {
    return reservationApi.get<HotelDetailResponse>(`/hotels/${hotelId}`);
}

export const createReservation = (request: ReservationRequest) => {
    return reservationApi.post<string>("/reservations", request)
}

export const getRoomTypeForReservation = (hotelId: number, roomTypeId: number, startDate: string, endDate: string, numberOfRooms: number) => {
    return reservationApi.get<RoomTypeReservationResponse>(`/hotels/${hotelId}/roomTypes/${roomTypeId}/reservation`,{
        params: {startDate, endDate, numberOfRooms}
    })
}

export const reservationConfirm = (reservationKey: string) => {
    return reservationApi.get<ReservationDetailResponse>(`/reservations/${reservationKey}`)
}

export const getMyInfo = () => {
    return reservationApi.get<UserInfoResponse>("/user/myInfo")
}

export const getMyReservations = (status: string) => {
    return reservationApi.get<ReservationResponse[]>("/reservations",{
        params: {status}
    })
}

export const cancelReservation = (reservationKey: string) => {
    return reservationApi.delete<void>(`/reservations/${reservationKey}`)
}

export const reservationInfo = (reservationKey: string) => {
    return reservationApi.get<ReservationInfoResponse>(`/reservations/${reservationKey}/reservation-info`)
}