import { createReservation } from "@/api/api";
import { getErrorCode, getErrorMessage } from "@/api/errorHelpers";
import { reservationKeys } from "@/features/mypage/hooks/reservationKeys";
import type { HotelDetailResponse } from "@/api/types/hotel";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useRef } from "react";
import { useNavigate } from "react-router";
import { toast } from "react-toastify";
import { hotelKeys } from "../../hotel/hooks/hotelkeys";
import * as Sentry from "@sentry/react"

interface UseReservationParams {
    hotelId?: string,
    data?: HotelDetailResponse;
    startDate: string;
    endDate: string;
    numberOfGuests: number;
    numberOfRooms: number;
}

export const useCreateReservation = ({ hotelId, data, startDate, endDate, numberOfGuests, numberOfRooms }: UseReservationParams) => {
    const navigate = useNavigate()
    const selectedRoomTypeIdRef = useRef<number | null>(null)
    const queryClient = useQueryClient()

    const reservationKey = useRef(crypto.randomUUID())

    const { mutate: createReservationMutate, isPending } = useMutation({
        mutationFn: createReservation,
        onSuccess: (res) => {
            queryClient.invalidateQueries({ queryKey: reservationKeys.myLists() })
            const { reservationKey, orderId } = res.data;
            const roomType = data?.roomTypes.find(r => r.roomTypeId === selectedRoomTypeIdRef.current);
            navigate(`/reservations/${reservationKey}/reservation-info`, {
                state: {
                    orderId,
                    reservationKey,
                    hotelName: data?.hotelName,
                    hotelAddress: data?.address,
                    roomTypeName: roomType?.name,
                    imageUrl: roomType?.imageUrl,
                    checkInTime: data?.checkInTime,
                    checkOutTime: data?.checkOutTime,
                    startDate,
                    endDate,
                    numberOfRooms,
                    numberOfGuests
                }
            });
        },
        onError: (err) => {
            const code = getErrorCode(err)
            if (code === "RESERVATION_UNAVAILABLE") {
                toast.info(getErrorMessage(err))
                queryClient.invalidateQueries({
                    queryKey: hotelKeys.detail({ hotelId, startDate, endDate, numberOfRooms, numberOfGuests })
                })
                return
            }
            if (code === "RESERVATION_CONFLICT") {
                toast.error(getErrorMessage(err))
                navigate(`/hotels/${hotelId}`)
                return
            }
            if (code === "INVALID_DATE_RANGE" || code === "EXCEED_MAX_OCCUPANCY") {
                Sentry.captureMessage(`[${code}] ${getErrorMessage(err)}`, "warning")
                toast.error("입력값을 다시 확인해주세요")
                return
            }
        }
    })

    const handleReservation = (roomTypeId: number) => {
        if (!data) return;
        selectedRoomTypeIdRef.current = roomTypeId;
        createReservationMutate({
            reservationKey: reservationKey.current,
            hotelId: Number(hotelId),
            roomTypeId,
            startDate,
            endDate,
            numberOfGuests,
            numberOfRooms
        });
    }

    return { handleReservation, isPending }

}