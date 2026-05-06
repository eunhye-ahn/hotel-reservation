export interface ReservationRequest {
    reservationKey: string,
    hotelId: number,
    roomTypeId: number,
    startDate: string,
    endDate: string,
    numberOfRoomsToReserve: number,
    numberOfGuests: number
}

export interface RoomTypeReservationResponse {
    priceToken: string,
    availableCount: number,
    demandRate: number
}