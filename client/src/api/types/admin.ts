
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
    displayReservationNO: number,
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
    page: number
}

export interface AdminPaymentResponse {
    displayOrderNO: string,
    hotelName: string,
    userName: string,
    amount: number,
    status: string,
    createdAt: string
}

export interface AdminSettlementSearchRequest {
    searchType?: string,
    keyword?: string,
    hasPendingBalance?: boolean,
    sortType?: string
    page: number
}

export interface AdminSettlementSearchResponse {
    hotelId: number,
    sellerAccount: string,
    hotelName: string,
    pendingBalance: number,
    lastSettledAt: string,
    totalSettlementAmount: number
}

export interface ExecuteSettlementRequest {
    periodStart: string,
    periodEnd: string
}

export interface SettlementHistorySearchRequest {
    startDate?: string,
    endDate?: string,
    status?: string,
    page?: number
}

export interface RoomTypeInventoryCalendarResponse {
    roomTypeId: number,
    roomTypeName: string,
    cells: InventoryCalendarCellResponse[]
}

export interface InventoryCalendarCellResponse {
    inventoryId: number,
    date: string,
    totalInventory: number,
    totalReserved: number,
    availableCount: number
}

export interface searchRoomInfoRequest {
    roomTypeId?: number,
    floor?: number,
    targetDate?: string,
    page: number
}

export interface AdminRoomInfoResponse {
    roomId: number,
    roomName: string,
    floor: number,
    roomNumber: number,
    roomTypeName: string,
    usable: boolean,
    assignable: boolean
}

export interface RoomFilterOptionResponse {
    floors: number[],
    roomTypes: RoomTypeOption[]
}

export interface RoomTypeOption {
    id: number,
    name: string
}

export interface DashBoardSummaryResponse {
    todayCheckInCount: number,
    checkInDiff: number,
    unassignedCount: number,
    todayPaymentAmount: number,
    todayPaymentCount: number,
    pendingHotelCount: number,
    totalPendingBalance: number,
    failedPaymentCount: number
}

export interface DailyStatisticsResponse {
    date: string,
    reservationCount: number,
    paymentCount: number,
    paymentTotal: number
}

export interface UnassignRoomInfo {
    reservationId: number,
    displayReservationNo: string,
    hotelName: string,
    startDate: string
}

export interface ReserveStatusStaticResponse {
    status: string,
    count: number
}

export interface PaymentStatusStaticResponse {
    status: string,
    count: number
}

export interface TopPendingBalanceHotel {
    hotelId: number,
    hotelName: string,
    pendingBalance: number
}