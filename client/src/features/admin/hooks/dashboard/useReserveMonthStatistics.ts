import { getReserveStatusByMonth } from "@/api/api"
import { useQuery } from "@tanstack/react-query"

export const useReserveMothStatistics = () => {
    const { data: reserveData, isLoading: isReserveLoading, isError: isReserveError } = useQuery({
        queryKey: ["month"],
        queryFn: () => getReserveStatusByMonth().then(res => res.data)
    })


    return {
        reserveData, isReserveError, isReserveLoading,
    }
}