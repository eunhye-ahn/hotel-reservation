import { getMyReservations } from "@/api/api"
import type { ReservationResponse, ReservationStatus } from "@/api/types/reservation"
import { useQuery } from "@tanstack/react-query"
import { reservationKeys } from "./reservationKeys"


export const useMyReservationList = (status: ReservationStatus, enabled: boolean) => {
    const { data: reservations, isLoading: isReservationListLoading } = useQuery<ReservationResponse[]>({
        queryKey: reservationKeys.myList(status),
        queryFn: () => getMyReservations(status).then((res) => res.data),
        enabled
    })

    return { reservations, isReservationListLoading }
}