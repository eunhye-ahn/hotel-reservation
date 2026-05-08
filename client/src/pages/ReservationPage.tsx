import { createReservation, getRoomTypeForReservation } from "@/axios/api";
import type { ReservationRequest, RoomTypeReservationResponse } from "@/type/reservation";
import { useEffect, useState } from "react";
import { useLocation, useNavigate, useParams } from "react-router"
import '@/pages/ReservationPage.css';
import { toast } from "react-toastify";

export const ReservationPage = () => {
    const { hotelId, roomTypeId } = useParams();
    const location = useLocation();
    const state = location.state;
    const navigate = useNavigate();

    const [data, setData] = useState<RoomTypeReservationResponse>();

    useEffect(() => {
        getRoomTypeForReservation(Number(hotelId), Number(roomTypeId), state.startDate, state.endDate, state.numberOfRooms)
            .then((res) => setData(res.data))
            .catch((err) => alert(err.message));
    }, []);

    const handleReservation = () => {
        if (!data) return;
        const reservationData: ReservationRequest = {
            reservationKey: crypto.randomUUID(),
            priceToken : data.priceToken,
            hotelId: Number(hotelId),
            roomTypeId: Number(roomTypeId),
            startDate: state.startDate,
            endDate: state.endDate,
            numberOfGuests: state.numberOfGuests,
            numberOfRooms: state.numberOfRooms
        };

        createReservation(reservationData)
        .then((res)=>navigate(`/reservations/${res.data}`))
        .catch((err)=>{
            const message = err.response.data.message;
            const code = err.response.data.code;

            switch(code){
                case 'PRICE_TOKEN_EXPIRED':
                case 'PRICE_TOKEN_NOT_FOUND':
                    toast.error(message);
                    navigate(-1);
                break;   
            }
        })
    }

return (
    <div className="reservation-container">
        <div className="reservation-notice">이 가격의 객실이 {data?.availableCount}개 남았어요</div>
        <div className="reservation-content">
            <div className="reservation-left">
                <div className="hotel-info">
                    <p className="hotel-name">{state.hotelName}</p>
                    <p className="hotel-address">{state.hotelAddress}</p>
                </div>
                <p className="room-type-name">{state.roomTypeName}</p>
                <div className="room-info">
                    <img className="room-image" src={state.imageUrl} />
                    <div className="room-price">
                        <p className="room-demand">{data?.demandRate.toLocaleString()}</p>
                    </div>
                </div>
                <div className="checkin-info">
                    <div className="checkin">
                        <p>체크인</p>
                        <p>{state.startDate}</p>
                        <p>{state.checkInTime?.substring(0, 5)}</p>
                    </div>
                    <div className="nights">1박</div>
                    <div className="checkout">
                        <p>체크아웃</p>
                        <p>{state.endDate}</p>
                        <p>{state.checkOutTime?.substring(0, 5)}</p>
                    </div>
                </div>
            </div>
            <div className="reservation-right">
                <div className="payment-box">
                    <div className="payment-row">
                        <span>결제금액</span>
                        <span>{data?.totalPrice.toLocaleString()}</span>
                    </div>
                    <button className="payment-button" onClick={handleReservation}>결제하기</button>
                </div>
            </div>
        </div>
    </div>
)}