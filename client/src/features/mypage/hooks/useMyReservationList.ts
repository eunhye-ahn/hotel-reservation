import { getMyReservations } from "@/api/api"
import type { ReservationResponse, ReservationStatus } from "@/type/reservation"
import { useQuery } from "@tanstack/react-query"


export const useMyReservationList = (status: ReservationStatus, enabled: boolean) => {
    const { data: reservations, isLoading: isReservationListLoading } = useQuery<ReservationResponse[]>({
        queryKey: ["myReservationList", status],
        queryFn: () => getMyReservations(status).then((res) => res.data),
        enabled
    })

    return { reservations, isReservationListLoading }
}