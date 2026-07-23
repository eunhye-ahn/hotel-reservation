import { getReservationDetail } from "@/api/api"
import { useQuery } from "@tanstack/react-query"

export const useReservationDetail = (reservationId: number) => {
    const { data, isLoading, isError } = useQuery({
        queryKey: ["admin-reserve-detail", reservationId],
        queryFn: () => getReservationDetail(reservationId).then(res => res.data)
    })

    return { data, isLoading, isError }
}