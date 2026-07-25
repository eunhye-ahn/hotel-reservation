import { getReservationDetail } from "@/api/api"
import { useQuery } from "@tanstack/react-query"
import { adminReservationKeys } from "./adminReservationKeys"

export const useReservationDetail = (reservationId: number) => {
    const { data, isLoading, isError } = useQuery({
        queryKey: adminReservationKeys.detail(reservationId),
        queryFn: () => getReservationDetail(reservationId).then(res => res.data)
    })

    return { data, isLoading, isError }
}