import { useLocation, useNavigate, useParams } from "react-router"
import '@/pages/ReservationPage.css';
import { useQuery } from "@tanstack/react-query";
import type { ReservationInfoResponse } from "@/type/reservation";
import { reservationInfo } from "@/axios/api";


export const ReservationPage = () => {
    const { reservationKey } = useParams<string>();
    const location = useLocation();
    const state = location.state;
    const navigate = useNavigate();

    if (!reservationKey) return null;
    const {data, isLoading, isError} = useQuery<ReservationInfoResponse>({
        queryKey: ["reservationInfo",reservationKey],
        queryFn: () => reservationInfo(reservationKey).then((res)=>res.data)
    });

return(
    <div className="reservation-container">
        {data?.availableCount == 0 ? "이 가격의 객실이 마지막이에요 !" :
        <div className="reservation-notice">이 가격의 객실이 {data?.availableCount}개 남았어요</div>
        }
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
                        <p className="room-demand">{data?.totalPrice.toLocaleString()}</p>
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
                    <button className="payment-button">
                        결제하기
                    </button>
                </div>
            </div>
        </div>
    </div>
)}