import { getHotelsByFilter } from "@/api/api"
import type { CursorResponse, HotelListFilter } from "@/type/hotel"
import { useInfiniteQuery } from "@tanstack/react-query"

export const useHotelList = (filter: HotelListFilter) => {
    const { data, isLoading, isError, fetchNextPage, hasNextPage } = useInfiniteQuery<CursorResponse>({
        queryKey: ["hotels", filter],
        queryFn: ({ pageParam }) => getHotelsByFilter(filter, pageParam as number ?? 0).then((res) => res.data),
        initialPageParam: undefined,
        getNextPageParam: (lastPage) => lastPage.nextCursor ?? undefined
    })

    return { data, isLoading, isError, fetchNextPage, hasNextPage }
}