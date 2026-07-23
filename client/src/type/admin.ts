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

export interface AdminReseervationSearchRequest {
    searchType?: string;
    keyword?: string;
    startDate?: string;
    endDate?: string;
    status?: string;
    roomAssigned?: boolean;
    page: number;
}

export interface AdminReservationDetailResponse {
    reservationKey: string,
    username: string,
    numberOfGuests: number,
    numberOfRooms: number,
    startDate: string,
    endDate: string,
    checkInTime: string,
    checkOutTime: string,
    totalPrice: number,
    paymentStatus: string,
    reservationStatus: string,
    hotelName: string,
    roomTypeName: string,
    createdAt: string,
    roomAssigned: boolean,
    roomNumber: number,
    roomName: string,
    floor: number,
    usable: boolean
}
