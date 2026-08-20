export interface ReservationRequest {
    hotelId: number,
    roomTypeId: number,
    startDate: string,
    endDate: string,
    numberOfGuests: number,
    numberOfRooms: number
}

export interface ReservationCreateResponse {
    reservationKey: string,
    orderId: string
}

export interface RoomTypeReservationResponse {
    availableCount: number,
    demandRate: number,
    totalPrice: number
}

export interface ReservationDetailResponse {
    reservationKey: string,
    displayReservationNO: string,
    hotelImageUrl: string,
    roomTypeImageUrl: string,
    hotelName: string,
    roomTypeName: string,
    startDate: string,
    endDate: string,
    checkInTime: string,
    checkOutTime: string,
    numberOfRooms: number,
    numberOfGuests: number,
    totalPrice: number,
    paymentStatus: string,
    createdAt: string,
    cancelType: string,
    cancelReason: string,
    reservationStatus: string,
}

export interface ReservationResponse {
    reservationKey: string,
    hotelImageUrl: string,
    hotelName: string,
    roomTypeName: string,
    startDate: string,
    endDate: string,
    checkInTime: string,
    checkOutTime: string,
    reservationStatus: string,
    paymentStatus: string,
    cancelType: string,
    cancelReason: string
}

export interface ReservationInfoResponse {
    availableCount: number,
    totalPrice: number
}

export type ReservationStatus = 'BEFORE_USE' | 'AFTER_USE' | 'CANCELED';