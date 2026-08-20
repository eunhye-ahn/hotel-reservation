import { assignRoom } from "@/api/api"
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { adminReservationKeys } from "./adminReservationKeys"
import { adminDashboardKeys } from "../dashboard/adminDashboardKeys"
import { handleDefenseError } from "@/api/errorHelpers"
import { adminInventoryKeys } from "../inventory/adminInventorykey"


export const useAssignRoom = (reservationId: number) => {
    const queryClient = useQueryClient()

    const { mutate: assignRoomMutate, isPending: isAssigning } = useMutation({
        mutationFn: (roomId: number) => assignRoom(reservationId, roomId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: adminReservationKeys.all })
            queryClient.invalidateQueries({ queryKey: adminDashboardKeys.all })
            queryClient.invalidateQueries({queryKey: adminInventoryKeys.all})
        },
        onError: (err) => {
            handleDefenseError(err, () => queryClient.invalidateQueries({
                queryKey: adminReservationKeys.rooms()
            }))
            return
        }
    })

    return { assignRoomMutate, isAssigning }
}