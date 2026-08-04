import { getHotelsByFilter } from "@/api/api"
import type { CursorResponse, HotelListFilter } from "@/api/types/hotel"
import { useInfiniteQuery } from "@tanstack/react-query"
import { hotelKeys } from "./hotelkeys"

export const useHotelList = (filter: HotelListFilter) => {
    const { data, isLoading, isError, fetchNextPage, hasNextPage } = useInfiniteQuery<CursorResponse>({
        queryKey: hotelKeys.list(filter),
        queryFn: ({ pageParam }) => getHotelsByFilter(filter, pageParam as number ?? 0).then((res) => res.data),
        initialPageParam: undefined,
        getNextPageParam: (lastPage) => lastPage.nextCursor ?? undefined
    })

    return { data, isLoading, isError, fetchNextPage, hasNextPage }
}