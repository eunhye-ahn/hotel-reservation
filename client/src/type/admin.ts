export interface AdminReservationSearchResponse {
    id: number,
    reservationKey: string,
    username: string,
    hotelName: string,
    startDate: string,
    endDate: string,
    reservationStatus: string,
    roomAssigned: boolean,
    createdDate: string
}