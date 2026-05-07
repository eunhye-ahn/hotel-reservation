
import type { AccessTokenResponse, LoginRequest, SignUpRequest } from "@/type/auth";
import { api } from "./axiosInstance"
import axios from "axios";
import type { HotelDetailResponse, hotelResponse, Page } from "@/type/hotel";
import type { ReservationDetailResponse, ReservationRequest, ReservationResponse, RoomTypeReservationResponse } from "@/type/reservation";
import type { UserInfoResponse } from "@/type/user";

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

export const getHotels = () => {
    return api.get<Page<hotelResponse>>("/hotels");
}

export const getHotelDetail = (hotelId: number) => {
    return api.get<HotelDetailResponse>(`/hotels/${hotelId}`);
}

export const createReservation = (request: ReservationRequest) => {
    return api.post<void>("/reservations", request)
}

export const getRoomTypeForReservation = (hotelId: number, roomTypeId: number, startDate: string, endDate: string, numberOfRooms: number) => {
    return api.get<RoomTypeReservationResponse>(`/hotels/${hotelId}/roomTypes/${roomTypeId}/reservation`,{
        params: {startDate, endDate, numberOfRooms}
    })
}

export const reservationConfirm = (reservationKey: string) => {
    return api.get<ReservationDetailResponse>(`/reservations/${reservationKey}`)
}

export const getMyInfo = () => {
    return api.get<UserInfoResponse>("/user/myInfo")
}

export const getMyReservations = (status: string) => {
    return api.get<ReservationResponse[]>("/reservations",{
        params: {status}
    })
}