import { getTopPendingHotels } from "@/api/api"
import { useQuery } from "@tanstack/react-query"

export const useTopPendingHotelList = () => {
    const { data: pendingHotelData, isLoading: isPendingHotelLoading, isError: isPendingHotelError } = useQuery({
        queryKey: ["pending"],
        queryFn: () => getTopPendingHotels().then(res => res.data)
    })


    return {
        pendingHotelData, isPendingHotelLoading, isPendingHotelError
    }
}