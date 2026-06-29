declare const kakao: any;

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
    hotelAddress: string
}

export interface WishListCollectionResponse {
    collectionId: number,
    name: string,
    items: WishListResponse[]
}

export interface AddWishListRequest {
    hotelId: number
}

export interface AddWishListResponse {
    collectionName: string,
    hotelImageUrl: string
}