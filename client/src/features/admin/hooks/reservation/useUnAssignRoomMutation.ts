import { unassignRoom } from "@/api/api"
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { adminReservationKeys } from "./adminReservationKeys"
import { adminDashboardKeys } from "../dashboard/adminDashboardKeys"

export const useUnassignRoomMutation = (reservationId: number) => {
    const queryClient = useQueryClient()

    const { mutate: unassignRoomMutate, isPending: isUnAssigning } = useMutation({
        mutationFn: () => unassignRoom(reservationId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: adminReservationKeys.rooms() })
            queryClient.invalidateQueries({ queryKey: adminReservationKeys.detail(reservationId) })
            queryClient.invalidateQueries({ queryKey: adminReservationKeys.lists() })
            queryClient.invalidateQueries({ queryKey: adminDashboardKeys.summary() })

        }
    })

    return { unassignRoomMutate, isUnAssigning }
}