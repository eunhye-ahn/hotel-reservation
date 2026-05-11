export interface ReservationRequest {
    reservationKey: string,
    priceToken: string,
    hotelId: number,
    roomTypeId: number,
    startDate: string,
    endDate: string,
    numberOfGuests: number,
    numberOfRooms: number
}

export interface RoomTypeReservationResponse {
    priceToken: string,
    availableCount: number,
    demandRate: number,
    totalPrice: number
}

export interface ReservationDetailResponse{
    reservationKey: string,
    imageUrl: string,
    hotelName: string,
    roomTypeName: string,
    startDate: string,
    endDate: string,
    checkInTime: string,
    checkOutTime: string,
    numberOfRooms: number,
    numberOfGuests: number,
    totalPrice: number,
    status: string,
    createdAt: string
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
    reservationStatus: string
}

export type ReservationStatus = 'BEFORE_USE' | 'AFTER_USE' | 'CANCELED';