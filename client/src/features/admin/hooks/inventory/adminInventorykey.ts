import type { searchRoomInfoRequest } from "@/api/types/admin";
import type { searchInventorySummaryRequest } from "@/type/hotel";

export const adminInventoryKeys = {
    all: ["admin-inventory"] as const,

    lists: () => [...adminInventoryKeys.all, "list"] as const,
    list: (filter: searchInventorySummaryRequest) => [...adminInventoryKeys.lists(), filter] as const,

    calendars: () => [...adminInventoryKeys.all, "calendar"] as const,
    calendar: (hotelId: number, startDate: string, endDate: string) =>
        [...adminInventoryKeys.calendars(), hotelId, startDate, endDate] as const,

    rooms: () => [...adminInventoryKeys.all, "room"] as const,
    room: (hotelId: number, filter: searchRoomInfoRequest) =>
        [...adminInventoryKeys.rooms(), hotelId, filter] as const,

    options: () => [...adminInventoryKeys.all, "filterOptions"] as const,
    option: (hotelId: number) => [...adminInventoryKeys.options(), hotelId] as const
}