import { useLocation, useNavigate, useParams } from "react-router"
import '@/pages/ReservationPage.css';
import { useQuery } from "@tanstack/react-query";
import type { ReservationInfoResponse } from "@/shared/type/reservation";
import { preparePayment } from "@/api/payment-service";
import { toast } from "react-toastify";
import { loadTossPayments } from "@tosspayments/tosspayments-sdk";
import { reservationInfo } from "@/api/reservation-service";
import { useRef, useState } from "react";

export const ReservationPage = () => {
    const { reservationKey } = useParams<string>();
    const location = useLocation();
    const state = location.state;
    const navigate = useNavigate();

    const idempotencyKey = useRef<string>(crypto.randomUUID());

    const {data, isLoading, isError} = useQuery<ReservationInfoResponse>({
        queryKey: ["reservationInfo",reservationKey],
        queryFn: () => reservationInfo(reservationKey!).then((res)=>res.data)
    });

    const handlePayment = async () => {
        try{
            //내 서버에서 paymentOrderId, amount 받아오기
            //오픈 -> 승인 결과 받은 후에 순서대로 실행되어야함
            const res = await preparePayment(reservationKey!, idempotencyKey.current);
            const {paymentOrderId, amount, userId} = res.data;

            //토스 결제창 오픈
            const tossPayments = await loadTossPayments(import.meta.env.VITE_TOSS_CLIENT_KEY);
            const payment = tossPayments.payment({ customerKey: String(userId) });

            await payment.requestPayment({
                method: "CARD",
                amount: {
                    currency: "KRW",
                    value: amount,
                },
                orderId: paymentOrderId,
                orderName: state.roomTypeName,
            successUrl: `${window.location.origin}/payments/success`,
            failUrl: `${window.location.origin}/payments/fail`,
            });

        }catch(err){
            //타임아웃 시 (또는 네트워크오류?) 같은멱등키로 시도 로직 추가
            console.log(err)
            toast.error("결제 중 오류가 발생했습니다")
            navigate("/");
        }
    }

    if(isLoading) return <p>loading...</p>
    if(isError) {
        toast.error("일시적인 오류가 발생했습니다");
        navigate("/");
    }
    if (!reservationKey) return null;

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
                    <button className="payment-button" onClick={handlePayment}>
                        결제하기
                    </button>
                </div>
            </div>
        </div>
    </div>
)}