import { cancelReservationByAdmin } from "@/api/api"
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { adminReservationKeys } from "./adminReservationKeys"
import { adminDashboardKeys } from "../dashboard/adminDashboardKeys"

export const useCancelByAdmin = (reservationId: number) => {
    const queryClient = useQueryClient()

    const { mutate: cancelByAdminMutate, isPending: isCanceling } = useMutation({
        mutationFn: (cancelReason: string) => cancelReservationByAdmin(reservationId, cancelReason),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: adminReservationKeys.rooms(reservationId) })
            queryClient.invalidateQueries({ queryKey: adminReservationKeys.detail(reservationId) })
            queryClient.invalidateQueries({ queryKey: adminReservationKeys.lists() })
            queryClient.invalidateQueries({ queryKey: adminDashboardKeys.summary })
        }
    })

    return { cancelByAdminMutate, isCanceling }
}