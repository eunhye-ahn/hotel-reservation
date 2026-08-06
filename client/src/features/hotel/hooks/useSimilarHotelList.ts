import { getSimilarHotel } from "@/api/api"
import type { RecentHotel } from "@/store/useRecentHotelStore"
import { useQuery } from "@tanstack/react-query"
import { hotelKeys } from "./hotelkeys"

export const useSimilarHotelList = ({ recentHotels }: { recentHotels: RecentHotel[] }) => {
    const { data, isLoading, isError } = useQuery({
        queryKey: hotelKeys.similar(recentHotels[0].hotelId),
        queryFn: () => getSimilarHotel(recentHotels[0].hotelId).then(res => res.data),
        enabled: recentHotels.length > 0
    })

    return { data, isLoading, isError }
}