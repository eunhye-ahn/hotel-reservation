import { getRoomsByReservation } from "@/api/api"
import { useQuery } from "@tanstack/react-query"
import { adminReservationKeys } from "./adminReservationKeys"

export const useRoomsByReservation = (reservationId: number) => {
    const { data, isLoading, isError } = useQuery({
        queryKey: adminReservationKeys.room(reservationId),
        queryFn: () => getRoomsByReservation(reservationId).then(res => res.data)
    })

    return { data, isLoading, isError }
}