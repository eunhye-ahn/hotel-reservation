
import type { ReservationResponse, ReservationStatus } from "@/type/reservation";
import type { UserInfoResponse } from "@/type/user";
import '@/css/MyPage.css';
import { useEffect, useState } from "react"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { toast } from "react-toastify";
import { useNavigate } from "react-router";
import { useAuthStore } from "@/store/useAuthStore";
import { cancelReservation, getMyInfo, getMyReservations } from "@/api/api";
import { WishList } from "@/component/WIshList";


export const MyPage = () => {
    const [status, setStatus] = useState<ReservationStatus>('BEFORE_USE');
    const navigate = useNavigate();
    
    const {accessToken} = useAuthStore();
    
    useEffect(()=>{
        if(!accessToken){
            navigate("/login")
        }
    },[])

    //내 정보조회
    const {data, isLoading, isError} = useQuery<UserInfoResponse>({
        queryKey: ["myInfo"],
        queryFn: ()=> getMyInfo().then((res)=> res.data),
        enabled: !!accessToken
    });
    const queryClient = useQueryClient();

    //내 예약조회
    const {data: reservation, isLoading: isReservationListLoading} =  useQuery<ReservationResponse[]>({
        queryKey: ["myReservationList", status],
        queryFn: ()=>getMyReservations(status).then((res)=>res.data),
        enabled: !!accessToken
    })

    //예약 취소
    const {mutate, isPending} = useMutation({
        mutationFn: cancelReservation,
        //캐시 무효화 -> useQuery가 stale 감지 -> queryFn 자동 재실행 -> 새 데이터로 화면 업데이트
        onSuccess: (()=>{
            queryClient.invalidateQueries({queryKey: ["myReservationList"]})
        }),
        onError: ((err: any)=>{
            const code = err.response.data.code;
            const message = err.response.data.message;
            if(code === "RESERVATION_NOT_FOUND"){
                toast.error(message)
            }
        })
    });

    if(isLoading || isReservationListLoading) return <p>Loading...</p>
    if(isError){
        toast.error("일시적인 오류가 발생했습니다")
        navigate(-1)
        return null;   
    }

    const getPaymentStatus = (status: string) => {
        if(status === "PENDING"){
            console.log(status)
            return "결제미완료"
        }
        else if(status === "PAID"){
             console.log(status)
            return "결제완료"
        }else if(status === "EXPIRED"){
            return "예약만료"
        }
    }

return (
    <div className="mypage-container">
        <div className="mypage-info">
            <p className="mypage-greeting">{data?.name}님 안녕하세요</p>
            <p className="mypage-email">{data?.email}</p>
            <p className="mypage-phone">{data?.phone}</p>
        </div>
        <WishList />
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
            {reservation?.map((reservation) => (
                    <div className="reservation-card" key={reservation.reservationKey}>
                        <div className="reservation-card-header">
                            <span className="reservation-card-status">{status === 'AFTER_USE' ? '이용완료' : status === 'BEFORE_USE' ? '이용전' : '취소'}</span>
                            <span>{getPaymentStatus(reservation.paymentStatus)}</span>
                            <div className="reservation-card-btns">
                                <button className="reservation-detail-btn" onClick={() => navigate(`/reservations/${reservation.reservationKey}`)}>상세보기</button>
                                <button className="reservation-cancel-btn" onClick={() => mutate(reservation.reservationKey)} disabled={isPending}>
                                    {isPending ? "취소 중..." : "예약취소"}
                                </button>
                            </div>                        </div>
                        <div className="reservation-card-body">
                            <img className="reservation-card-image" src={reservation.hotelImageUrl} />
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