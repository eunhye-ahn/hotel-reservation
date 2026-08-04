import { cancelReservationByAdmin } from "@/api/api"
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { adminReservationKeys } from "./adminReservationKeys"
import { adminDashboardKeys } from "../dashboard/adminDashboardKeys"
import { getErrorCode, getErrorMessage, handleDefenseError } from "@/api/errorHelpers"
import { toast } from "react-toastify"

export const useCancelByAdmin = (reservationId: number) => {
    const queryClient = useQueryClient()

    const { mutate: cancelByAdminMutate, isPending: isCanceling } = useMutation({
        mutationFn: (cancelReason: string) => cancelReservationByAdmin(reservationId, cancelReason),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: adminReservationKeys.room(reservationId) })
            queryClient.invalidateQueries({ queryKey: adminReservationKeys.detail(reservationId) })
            queryClient.invalidateQueries({ queryKey: adminReservationKeys.lists() })
            queryClient.invalidateQueries({ queryKey: adminDashboardKeys.summary() })
        },
        onError: (err) => {
            const code = getErrorCode(err)

            if (code === "INVALID_RESTORE") {
                handleDefenseError(err, () => {
                    queryClient.invalidateQueries({ queryKey: adminReservationKeys.lists() })
                })
                return
            }
            if (code === "CANNOT_CANCEL_RESERVATION" || code === "CANNOT_REFUND_RESERVATION") {
                handleDefenseError(err, () => queryClient.invalidateQueries({
                    queryKey: adminReservationKeys.lists()
                }))
                return
            }

            toast.error(getErrorMessage(err))
        }
    })

    return { cancelByAdminMutate, isCanceling }
}