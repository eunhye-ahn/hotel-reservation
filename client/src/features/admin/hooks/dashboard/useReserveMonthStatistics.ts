import { getReserveStatusByMonth } from "@/api/api"
import { useQuery } from "@tanstack/react-query"
import { adminDashboardKeys } from "./adminDashboardKeys"

export const useReserveMothStatistics = () => {
    const { data: reserveData, isLoading: isReserveLoading, isError: isReserveError } = useQuery({
        queryKey: adminDashboardKeys.reservationStatusMonth(),
        queryFn: () => getReserveStatusByMonth().then(res => res.data)
    })


    return {
        reserveData, isReserveError, isReserveLoading,
    }
}