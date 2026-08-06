import { useQuery } from "@tanstack/react-query"
import { hotelKeys } from "./hotelkeys"
import { getPopularHotel } from "@/api/api"

export const usePopularHotel = () => {
    return useQuery({
        queryKey: hotelKeys.popular(),
        queryFn: () => getPopularHotel().then(res => res.data)
    })
}