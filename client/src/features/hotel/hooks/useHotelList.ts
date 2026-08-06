import { getHotelsByFilter } from "@/api/api"
import type { HotelListFilter } from "@/api/types/hotel"
import { useQuery } from "@tanstack/react-query"
import { hotelKeys } from "./hotelkeys"

export const useHotelList = (filter: HotelListFilter, page: number) => {
    console.log('page prop:', page, 'queryKey:', hotelKeys.list(filter, page))
    const { data, isLoading, isError } = useQuery({
        queryKey: hotelKeys.list(filter, page),
        queryFn: () => getHotelsByFilter(filter, page).then((res) => res.data),
    })

    return { data, isLoading, isError }
}