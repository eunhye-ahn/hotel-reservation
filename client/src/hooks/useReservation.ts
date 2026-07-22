import { createReservation } from "@/api/api";
import type { HotelDetailResponse } from "@/type/hotel";
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { useRef } from "react";
import { useNavigate } from "react-router";
import { toast } from "react-toastify";

interface UseReservationParams {
    hotelId?: string,
    data?: HotelDetailResponse;
    startDate: string;
    endDate: string;
    numberOfGuests: number;
    numberOfRooms: number;
}

export const useReservation = ({hotelId, data, startDate, endDate, numberOfGuests, numberOfRooms}: UseReservationParams) => {
    const navigate = useNavigate()
    const selectedRoomTypeIdRef = useRef<number|null>(null)
    const queryClient = useQueryClient()

    const reservationKey = useRef(crypto.randomUUID())

    const {mutate : createReservationMutate, isPending} = useMutation({
        mutationFn: createReservation,
        onSuccess: (res)=>{
            queryClient.invalidateQueries({queryKey: ["myReservationList"]})
            const {reservationKey, orderId} =res.data;
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
        //에러코드분기
        onError: (err: any) => {
            const message = err.response.data.message;
            const code = err.response.data.code;

            switch(code){
                case 'PRICE_TOKEN_EXPIRED':
                case 'PRICE_TOKEN_NOT_FOUND':
                case 'RESERVATION_CONFLICT':
                case 'IDEMPOTENCY_NOT_FOUND':
                case 'IDEMPOTENCY_FAILED':
                case 'IDEMPOTENCY_REQUEST_MISMATCH':
                case 'IDEMPOTENCY_USER_MISMATCH':
                case 'HASH_GENERATION_FAILED':
                case 'IDEMPOTENCY_UNKNOWN':
                case 'IDEMPOTENCY_PROCESSING':
                case 'INVALID_INPUT':
                    toast.error(message);
                    navigate(`/hotels/${hotelId}`);
                    break;
                default:
                    toast.error("일시적인 오류가 발생했습니다")
                    navigate(`/hotels/${hotelId}`)   
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

    return {handleReservation, isPending}

}