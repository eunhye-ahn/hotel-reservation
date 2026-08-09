
import type { ReservationStatus } from "@/api/types/reservation";
import { useEffect, useState } from "react"
import { toast } from "react-toastify";
import { useNavigate } from "react-router";
import { useAuthStore } from "@/store/useAuthStore";
import { WishList } from "@/features/mypage/component/WishList";
import { useMyReservationList } from "../hooks/useMyReservationList";
import { useMyInfo } from "../hooks/useMyInfo";
import { ReservationCard } from "../component/ReservationCard";
import { Spinner } from "@/common/component/Spinner";
import { userCancelReservationByUser } from "../hooks/useCancelReservationByUser";
import { Pagination } from "@/common/component/Pagination";


export const MyPage = () => {
    const [status, setStatus] = useState<ReservationStatus>('BEFORE_USE')
    const navigate = useNavigate()
    const [page, setPage] = useState(0)

    const { accessToken } = useAuthStore()

    useEffect(() => {
        if (!accessToken) {
            navigate("/login")
        }
    }, [])

    const { myInfo, isMyInfoLoading, isMyInfoError } = useMyInfo(!!accessToken)

    const { reservations, isReservationListLoading } = useMyReservationList(status, page, !!accessToken)
    console.log(reservations)

    const { isCanceling, reserationCacelMutate } = userCancelReservationByUser()

    if (isMyInfoLoading || isReservationListLoading) return <Spinner />
    if (isMyInfoError) {
        toast.error("일시적인 오류가 발생했습니다")
        navigate(-1)
        return null
    }

    const handlePageChange = (newPage: number) => {
        setPage(newPage)
    }

    const tabs: { key: ReservationStatus; label: string }[] = [
        { key: 'BEFORE_USE', label: '이용전' },
        { key: 'AFTER_USE', label: '이용후' },
        { key: 'CANCELED', label: '취소됨' },
    ]

    console.log(reservations)

    return (
        <div className="detail-container">
            <div className="mt-8">
                <p className="text-xl font-bold mb-1">{myInfo?.name}님 안녕하세요</p>
                <p className="text-sm text-gray-500">{myInfo?.email}</p>
                <p className="text-sm text-gray-500">{myInfo?.phone}</p>
            </div>
            <div className="mt-8">
                <WishList />
            </div>
            <div className="mt-8">
                <div className="flex gap-2 mb-6">
                    {tabs.map(tab => (
                        <button
                            key={tab.key}
                            className={`px-4 py-2 w-full border text-sm cursor-pointer ${status === tab.key
                                ? "bg-gray-900 text-white border-gray-900"
                                : "border-gray-300 hover:bg-gray-50"
                                }`}
                            onClick={() => setStatus(tab.key)}
                        >
                            {tab.label}
                        </button>
                    ))}
                </div>
                <div className="flex flex-col gap-5">
                    {reservations?.content.length === 0 ? "예약내역이 없습니다"
                        : reservations?.content.map((reservation) => (
                            <ReservationCard
                                key={reservation.reservationKey}
                                reservation={reservation}
                                isPending={isCanceling}
                                onDetailClick={() => navigate(`/reservations/${reservation.reservationKey}`)}
                                onCancelClick={() => reserationCacelMutate(reservation.reservationKey)}
                                status={status}
                            />
                        ))}
                </div>
                <Pagination
                    page={reservations?.number ?? 0}
                    totalPages={reservations?.totalPages}
                    isFirst={reservations?.first}
                    isLast={reservations?.last}
                    onPageChange={handlePageChange}
                />
            </div>
        </div>
    )
}