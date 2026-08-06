import { assignRoom } from "@/api/api"
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { adminReservationKeys } from "./adminReservationKeys"
import { adminDashboardKeys } from "../dashboard/adminDashboardKeys"
import { handleDefenseError } from "@/api/errorHelpers"


export const useAssignRoom = (reservationId: number) => {
    const queryClient = useQueryClient()

    const { mutate: assignRoomMutate, isPending: isAssigning } = useMutation({
        mutationFn: (roomId: number) => assignRoom(reservationId, roomId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: adminReservationKeys.rooms() })
            queryClient.invalidateQueries({ queryKey: adminReservationKeys.detail(reservationId) })
            queryClient.invalidateQueries({ queryKey: adminReservationKeys.lists() })
            queryClient.invalidateQueries({ queryKey: adminDashboardKeys.summary() })
            queryClient.invalidateQueries({ queryKey: adminDashboardKeys.unAssignRoom() })
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