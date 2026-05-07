import { getMyInfo, getMyReservations } from "@/axios/api";
import type { ReservationResponse, ReservationStatus } from "@/type/reservation";
import type { UserInfoResponse } from "@/type/user";
import '@/pages/MyPage.css';
import { useEffect, useState } from "react"

export const MyPage = () => {
    const [data, setData] = useState<UserInfoResponse>();
    const [status, setStatus] = useState<ReservationStatus>('BEFORE_USE');
    const [reservation, setReservation] = useState<ReservationResponse[]>();

    useEffect(()=>{
        getMyInfo()
        .then((res)=>setData(res.data))
        .catch((err)=>alert(err.message))
    },[]);

    useEffect(() => {
        getMyReservations(status)
            .then((res) => setReservation(res.data))
            .catch((err) => alert(err.message));
    }, [status]);

return (
    <div className="mypage-container">
        <div className="mypage-info">
            <p className="mypage-greeting">{data?.name}님 안녕하세요</p>
            <p className="mypage-email">{data?.email}</p>
            <p className="mypage-phone">{data?.phone}</p>
        </div>
        <div className="mypage-reservations">
            <div className="mypage-tabs">
                <button
                    className={status === 'BEFORE_USE' ? 'active' : ''}
                    onClick={() => setStatus('BEFORE_USE')}>이용전</button>
                <button
                    className={status === 'AFTER_USE' ? 'active' : ''}
                    onClick={() => setStatus('AFTER_USE')}>이용후</button>
                <button
                    className={status === 'CANCELED' ? 'active' : ''}
                    onClick={() => setStatus('CANCELED')}>취소됨</button>
            </div>
            {reservation
                ?.filter((r) => r.reservationStatus === status)
                .map((reservation) => (
                    <div className="reservation-card" key={reservation.reservationKey}>
                        <div className="reservation-card-header">
                            <span className="reservation-card-status">{status === 'AFTER_USE' ? '이용완료' : status === 'BEFORE_USE' ? '이용전' : '취소됨'}</span>
                            <button className="reservation-detail-btn">상세보기</button>
                        </div>
                        <div className="reservation-card-body">
                            <img className="reservation-card-image" src={reservation.imageUrl} />
                            <div className="reservation-card-info">
                                <p className="reservation-hotel">{reservation.hotelName}</p>
                                <p className="reservation-room">{reservation.roomTypeName} &nbsp; 1박</p>
                            </div>
                            <div className="reservation-card-dates">
                                <p>{reservation.startDate}~{reservation.endDate} | 1박</p>
                                <p>체크인 {reservation.checkInTime.substring(0, 5)} | 체크아웃 {reservation.checkOutTime.substring(0, 5)}</p>
                            </div>
                        </div>
                    </div>
                ))}
        </div>
    </div>
)
}