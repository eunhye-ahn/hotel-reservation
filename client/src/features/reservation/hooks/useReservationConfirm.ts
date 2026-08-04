import { reservationConfirm } from "@/api/api"
import type { ReservationDetailResponse } from "@/api/types/reservation"
import { reservationKeys } from "@/features/mypage/hooks/reservationKeys"
import { useQuery } from "@tanstack/react-query"


export const useReservationConfirm = ({ reservationKey }: { reservationKey: string }) => {

    const { data, isLoading, isError, error } = useQuery<ReservationDetailResponse>({
        queryKey: reservationKeys.confirm(reservationKey),
        queryFn: () => reservationConfirm(reservationKey).then((res) => res.data)
    })

    return { data, isLoading, isError, error }
}