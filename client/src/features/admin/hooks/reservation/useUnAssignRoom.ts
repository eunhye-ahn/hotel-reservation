import { unassignRoom } from "@/api/api"
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { adminReservationKeys } from "./adminReservationKeys"

export const useUnassignRoom = (reservationId: number) => {
    const queryClient = useQueryClient()

    const { mutate: unassignRoomMutate, isPending: isUnAssigning } = useMutation({
        mutationFn: () => unassignRoom(reservationId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: adminReservationKeys.rooms(reservationId) })
            queryClient.invalidateQueries({ queryKey: adminReservationKeys.detail(reservationId) })
            queryClient.invalidateQueries({ queryKey: adminReservationKeys.lists() })
        }
    })

    return { unassignRoomMutate, isUnAssigning }
}