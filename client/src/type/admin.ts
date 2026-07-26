export interface AdminReservationSearchResponse {
    id: number,
    displayReservationNO: string,
    username: string,
    hotelName: string,
    startDate: string,
    endDate: string,
    roomTypeName: string,
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

export interface AdminRoomResponse {
    id: number,
    roomTypeName: string,
    roomName: string,
    roomNumber: number,
    floor: number,
    roomStatus: boolean,
    available: boolean,
    currentlyAssigned: boolean
}

export interface AssignmentRoomRequest {
    roomId: number
}

export interface CancelReservationByAdminRequest {
    cancelReason: string
}

export interface AdminPaymentSearchRequest {
    searchType?: string,
    keyword?: string,
    startDate?: string,
    endDate?: string,
    status?: string,
    page?: number
}

export interface AdminPaymentResponse {
    displayOrderNO: string,
    hotelName: string,
    userName: string,
    amount: number,
    status: string,
    createdAt: string
}