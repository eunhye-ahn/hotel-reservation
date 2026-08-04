import { cancelReservation } from "@/api/api";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "react-toastify";
import { reservationKeys } from "./reservationKeys";

export const userCancelReservationByUser = () => {
    const queryClient = useQueryClient()

    const { mutate: reserationCacelMutate, isPending: isCanceling } = useMutation({
        mutationFn: cancelReservation,
        onSuccess: (() => {
            queryClient.invalidateQueries({ queryKey: reservationKeys.myLists() })
        }),
        onError: ((err: any) => {
            const code = err.response.data.code;
            const message = err.response.data.message;
            if (code === "RESERVATION_NOT_FOUND") {
                toast.error(message)
            }
        })
    })
    return { reserationCacelMutate, isCanceling }
}
