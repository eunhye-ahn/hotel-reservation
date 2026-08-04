import { getTopPendingHotels } from "@/api/api"
import { useQuery } from "@tanstack/react-query"
import { adminDashboardKeys } from "./adminDashboardKeys"

export const useTopPendingHotelList = () => {
    const { data: pendingHotelData, isLoading: isPendingHotelLoading, isError: isPendingHotelError } = useQuery({
        queryKey: adminDashboardKeys.pendingHotels(),
        queryFn: () => getTopPendingHotels().then(res => res.data)
    })


    return {
        pendingHotelData, isPendingHotelLoading, isPendingHotelError
    }
}