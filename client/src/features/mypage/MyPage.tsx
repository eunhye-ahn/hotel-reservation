
import type { ReservationStatus } from "@/type/reservation";
import '@/css/MyPage.css';
import { useEffect, useState } from "react"
import { useMutation, useQueryClient } from "@tanstack/react-query";
import { toast } from "react-toastify";
import { useNavigate } from "react-router";
import { useAuthStore } from "@/store/useAuthStore";
import { cancelReservation } from "@/api/api";
import { WishList } from "@/common/component/WishList";
import { useMyReservationList } from "./hooks/useMyReservationList";
import { useMyInfo } from "./hooks/useMyInfo";
import { ReservationCard } from "./component/ReservationCard";
import { Spinner } from "@/common/component/Spinner";


export const MyPage = () => {
    const [status, setStatus] = useState<ReservationStatus>('BEFORE_USE');
    const navigate = useNavigate();

    const { accessToken } = useAuthStore();

    useEffect(() => {
        if (!accessToken) {
            navigate("/login")
        }
    }, [])

    const { myInfo, isMyInfoLoading, isMyInfoError } = useMyInfo(!!accessToken)
    const queryClient = useQueryClient()

    const { reservations, isReservationListLoading } = useMyReservationList(status, !!accessToken)

    //예약 취소 => 테스트 필요
    const { mutate, isPending } = useMutation({
        mutationFn: cancelReservation,
        onSuccess: (() => {
            queryClient.invalidateQueries({ queryKey: ["myReservationList"] })
        }),
        onError: ((err: any) => {
            const code = err.response.data.code;
            const message = err.response.data.message;
            if (code === "RESERVATION_NOT_FOUND") {
                toast.error(message)
            }
        })
    })

    if (isMyInfoLoading || isReservationListLoading) return <Spinner />
    if (isMyInfoError) {
        toast.error("일시적인 오류가 발생했습니다")
        navigate(-1)
        return null;
    }

    return (
        <div className="mypage-container">
            <div className="mypage-info">
                <p className="mypage-greeting">{myInfo?.name}님 안녕하세요</p>
                <p className="mypage-email">{myInfo?.email}</p>
                <p className="mypage-phone">{myInfo?.phone}</p>
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
                {reservations?.map((reservation) => (
                    <ReservationCard
                        key={reservation.reservationKey}
                        reservation={reservation}
                        isPending={isPending}
                        onDetailClick={() => navigate(`/reservations/${reservation.reservationKey}`)}
                        onCancelClick={() => mutate(reservation.reservationKey)}
                        status={status}
                    />
                ))}
            </div>
        </div>
    )
}