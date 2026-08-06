export interface hotelResponse {
    hotelId: number,
    name: string,
    maxRate: number,
    demandRate: number,
    discountRate: number,
    checkInTime: string,
    address: string,
    imageUrl: string
}

export interface Page<T> {
    content: T[],
    totalElements: number,
    totalPages: number,
    number: number,
    size: number,
    first: boolean,
    last: boolean
}

export interface HotelDetailResponse {
    hotelId: number,
    hotelName: string,
    address: string,
    imageUrl: string,
    checkInTime: string,
    checkOutTime: string,
    roomTypes: RoomTypeResponse[]
}

export interface RoomTypeResponse {
    roomTypeId: number,
    name: string,
    maxOccupancy: number,
    imageUrl: string,
    maxRate: number,
    demandRate: number,
    discountRate: number,
    availableCount: number
}

export interface CursorResponse {
    content: hotelResponse[],
    nextCursor: number,
    hasNext: boolean
}

export interface WishCollectionsRequest {
    collectionName: string
}

export interface WishListResponse {
    wishListItemId: number,
    hotelName: string,
    hotelImageUrl: string,
    hotelAddress: string,
    hotelId: number
}

export interface WishListCollectionResponse {
    collectionId: number,
    name: string,
    items: WishListResponse[],
    count: number
}

export interface AddWishListRequest {
    hotelId: number
}

export interface AddWishListResponse {
    collectionName: string,
    hotelImageUrl: string
}

export interface MoveWishRequest {
    collectionId: number,
    listId: number
}

export interface MoveWishResponse {
    collectionName: string,
    hotelImageUrl: string
}

export interface HotelListFilter {
    q: string,
    regionCode: string,
    subRegionCode: string,
    checkIn: string,
    checkOut: string,
    numberOfGuests: number,
    numberOfRooms: number,
    lclsSystm2: string | null
}

export interface SettlementHistoryResponse {
    settlementId: number,
    amount: number,
    periodStartDate: string,
    periodEndDate: string,
    status: string,
    settledAt: string,
    createdAt: string
}

export interface searchInventorySummaryRequest {
    date?: string,
    hotelName?: string,
    sortType?: string,
    page?: number
}

export interface AdminInventorySummaryResponse {
    hotelId: number,
    hotelName: string,
    ldongSignguCd: string,
    roomTypeCount: number,
    totalInventory: number,
    totalReserved: number,
    availableCount: number,
    reserveRate: number
}