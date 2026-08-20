import { unassignRoom } from "@/api/api"
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { adminReservationKeys } from "./adminReservationKeys"
import { adminDashboardKeys } from "../dashboard/adminDashboardKeys"
import { adminInventoryKeys } from "../inventory/adminInventorykey"

export const useUnassignRoomMutation = (reservationId: number) => {
    const queryClient = useQueryClient()

    const { mutate: unassignRoomMutate, isPending: isUnAssigning } = useMutation({
        mutationFn: () => unassignRoom(reservationId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: adminReservationKeys.all })
            queryClient.invalidateQueries({ queryKey: adminDashboardKeys.all })
            queryClient.invalidateQueries({queryKey: adminInventoryKeys.all})
        }
    })

    return { unassignRoomMutate, isUnAssigning }
}