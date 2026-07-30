import { searchByRoomInfo } from "@/api/api"
import type { searchRoomInfoRequest } from "@/type/admin"
import { useQuery } from "@tanstack/react-query"
import { adminInventoryKeys } from "./adminInventorykey"

export const useRoomList = (hotelId: number, filter: searchRoomInfoRequest) => {
    const { data: roomListData, isLoading: isRoomListLoading, isError: isRoomListError } = useQuery({
        queryKey: [adminInventoryKeys.room(hotelId, filter)],
        queryFn: () => searchByRoomInfo(hotelId, filter).then(res => res.data)
    })

    return { roomListData, isRoomListLoading, isRoomListError }
}