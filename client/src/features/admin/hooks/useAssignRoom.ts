import { assignRoom } from "@/api/api"
import { useMutation, useQueryClient } from "@tanstack/react-query"

export const useAssignRoom = (reservationId: number) => {
    const queryClient = useQueryClient()

    const { mutate: assignRoomMutate, isPending } = useMutation({
        mutationFn: (roomId: number) => assignRoom(reservationId, roomId),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["rooms", reservationId] })
            queryClient.invalidateQueries({ queryKey: ["admin-reserve-detail", reservationId] })
            queryClient.invalidateQueries({ queryKey: ["reservations"] })
        }
    })

    return { assignRoomMutate, isPending }
}