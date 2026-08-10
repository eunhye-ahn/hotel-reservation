import { getSimilarHotel } from "@/api/api"
import type { RecentHotel } from "@/store/useRecentHotelStore"
import { useQuery } from "@tanstack/react-query"
import { hotelKeys } from "./hotelkeys"

export const useSimilarHotelList = ({ recentHotels }: { recentHotels: RecentHotel[] }) => {
    const hotelId = recentHotels[0]?.hotelId

    const { data, isLoading, isError } = useQuery({
        queryKey: hotelKeys.similar(hotelId ?? 0),
        queryFn: () => getSimilarHotel(hotelId!).then(res => res.data),
        enabled: !!hotelId,
    })

    return { data, isLoading, isError }
}