import { assignRoom } from "@/api/api"
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { adminReservationKeys } from "./adminReservationKeys"
import { adminDashboardKeys } from "../dashboard/adminDashboardKeys"

export const useAssignRoom = (reservationId: number) => {
    const queryClient = useQueryClient()

    const { mutate: assignRoomMutate, isPending: isAssigning } = useMutation({
        mutationFn: (roomId: number) => assignRoom(reservationId, roomId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: adminReservationKeys.rooms(reservationId) })
            queryClient.invalidateQueries({ queryKey: adminReservationKeys.detail(reservationId) })
            queryClient.invalidateQueries({ queryKey: adminReservationKeys.lists() })
            queryClient.invalidateQueries({ queryKey: adminDashboardKeys.summary })
        }
    })

    return { assignRoomMutate, isAssigning }
}