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