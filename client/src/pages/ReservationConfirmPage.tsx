import { reservationConfirm } from "@/axios/api";
import type { ReservationDetailResponse } from "@/type/reservation";
import { useEffect, useState } from "react";
import { useParams } from "react-router"
import dayjs from 'dayjs';
import '@/pages/ReservationConfirmPage.css';

export const ReservationConfirmPage = () => {
    const {reservationKey} = useParams();

    const [data,setData] = useState<ReservationDetailResponse>();

    useEffect(()=>{
        if(!reservationKey) return;
        reservationConfirm(reservationKey)
        .then((res)=>setData(res.data))
        .catch((err)=>alert(err.message))
    },[]);

    const numberOfNights = dayjs(data?.endDate).diff(dayjs(data?.startDate), 'day');

return (
    <div className="confirm-container">
        <div className="confirm-header">예약 확인서</div>

        <div className="confirm-order">
            <div className="confirm-order-row">
                <span>예약번호</span>
                <span>{data?.reservationKey}</span>
            </div>
            <div className="confirm-order-row">
                <span>거래 일시</span>
                <span>{data?.createdAt}</span>
            </div>
        </div>

        <div className="confirm-section-title">상품 및 이용정보</div>
        <hr />

        <div className="confirm-booking">
            <img className="confirm-image" src={data?.imageUrl} />
            <div className="confirm-info">
                <p>{data?.hotelName}</p>
                <p>{data?.roomTypeName}</p>
            </div>
            <span className="confirm-status">결제완료</span>
        </div>

        <div className="confirm-dates">
            <p>{data?.startDate} ~{data?.endDate} | {numberOfNights}박</p>
            <p>체크인 {data?.checkInTime?.substring(0, 5)} | 체크아웃 {data?.checkOutTime?.substring(0, 5)}</p>
        </div>

        <div className="confirm-section-title">결제상세</div>
        <hr />

        <div className="confirm-payment-detail">
            <div className="confirm-payment-row">
                <span></span>
                <span>X {numberOfNights}박</span>
            </div>
            <div className="confirm-payment-row">
                <span></span>
                <span>X 객실 {data?.numberOfRooms}개</span>
            </div>
            <hr />
            <div className="confirm-payment-total">
                <span>총 결제금액</span>
                <span>{data?.totalPrice.toLocaleString()}</span>
            </div>
        </div>
    </div>
)}