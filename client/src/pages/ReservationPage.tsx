import { getRoomTypeForReservation } from "@/axios/api";
import type { RoomTypeReservationResponse } from "@/type/reservation";
import { useEffect, useState } from "react";
import { useLocation, useParams } from "react-router"

export const ReservationPage = () => {
    const { hotelId, roomTypeId } = useParams();
    const location = useLocation();
    const state = location.state;

    const [data, setData] = useState<RoomTypeReservationResponse>();

    useEffect(() => {
        getRoomTypeForReservation(Number(hotelId), Number(roomTypeId), state.startDate, state.endDate)
            .then((res) => setData(res.data))
            .catch((err) => alert(err.message));
    }, []);

    // const handleReservation = (roomTypeId: number) => {
    //     if (!data) return;
    //     const reservationData: ReservationRequest = {
    //         reservationKey: crypto.randomUUID(),
    //         hotelId: data.hotelId,
    //         roomTypeId: roomTypeId,
    //         startDate: startDate,
    //         endDate: endDate,
    //         numberOfRoomsToReserve: numberOfRooms,
    //         numberOfGuests: numberOfGuests
    //     };

    //     createReservation(reservationData);
    // }

    return (
        <div>
            <div>이 가격의 객실이 {data?.availableCount}개 남았어요</div>
            <div>
                <p>{state.hotelName}</p>
                <p>{state.hotelAddress}</p>
                <p>{state.roomTypeName}</p>
                <img src={state.imageUrl} />
                <p>{data?.demandRate}</p>
            </div>
            <div>
                <p>체크인</p>
                <p>{state.startDate}</p>
                <p>{state.checkInTime}</p>
                <p>1박</p>
                <p>체크아웃</p>
                <p>{state.endDate}</p>
                <p>{state.checkOutTime}</p>
            </div>
            <div>
                <p>결제금액</p>
                <p>{data?.demandRate}</p>
            </div>
        </div>
    )
}