import type { UseHotelDetailProps } from "@/features/hotel/hooks/useHotelDetail"
import type { HotelListFilter } from "@/api/types/hotel"

export const hotelKeys = {
    all: ["hotels"] as const,
    lists: () => [...hotelKeys.all, "list"] as const,
    list: (filter: HotelListFilter) => [...hotelKeys.lists(), filter] as const,

    details: () => [...hotelKeys.all, "detail"] as const,
    detail: (params: UseHotelDetailProps) => [...hotelKeys.details(), params] as const,

    similars: () => [...hotelKeys.all, "similar"] as const,
    similar: (hotelId?: number) => [...hotelKeys.similars(), hotelId] as const,

    wishes: () => [...hotelKeys.all, "wish"] as const,
    wish: (hotelId: number) => [...hotelKeys.wishes(), hotelId] as const,
}