import { getMyReservations } from "@/api/api"
import type { ReservationStatus } from "@/api/types/reservation"
import { useQuery } from "@tanstack/react-query"
import { reservationKeys } from "./reservationKeys"


export const useMyReservationList = (status: ReservationStatus, page: number, enabled: boolean) => {
    const { data: reservations, isLoading: isReservationListLoading } = useQuery({
        queryKey: reservationKeys.myList(status, page),
        queryFn: () => getMyReservations(status, page).then((res) => res.data),
        enabled
    })

    return { reservations, isReservationListLoading }
}