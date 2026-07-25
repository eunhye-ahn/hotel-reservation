import { getRoomsByReservation } from "@/api/api"
import { useQuery } from "@tanstack/react-query"

export const useRoomsByReservation = (reservationId: number) => {
    const { data, isLoading, isError } = useQuery({
        queryKey: ["rooms", reservationId],
        queryFn: () => getRoomsByReservation(reservationId).then(res => res.data)
    })

    return { data, isLoading, isError }
}