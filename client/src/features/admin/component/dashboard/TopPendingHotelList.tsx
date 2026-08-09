import { useNavigate } from "react-router"
import { useTopPendingHotelList } from "../../hooks/dashboard/useTopPendingHotelList"
import { Spinner } from "@/common/component/Spinner"
import { ErrorMessage } from "@/common/component/ErrorMessage"

export const TopPendingHotelList = () => {
    const { pendingHotelData, isPendingHotelError, isPendingHotelLoading } = useTopPendingHotelList()

    const navigate = useNavigate()

    if (isPendingHotelLoading) return <Spinner />
    if (isPendingHotelError) return <ErrorMessage />

    return (
        <div>
            <div className="flex items-center justify-between px-5 py-3 bg-gray-50 border-b border-gray-200">
                <span className="font-semibold text-sm">미정산 잔액 TOP 5</span>
            </div>
            <div className="divide-y divide-gray-100">
                {pendingHotelData?.length === 0 ? <p className="text-xs text-center mt-5">미정산된 잔액이 없습니다</p>
                    : pendingHotelData?.map(h => (
                        <div key={h.hotelId}
                            onClick={() => navigate(`/admin/settlements/${h.hotelId}`)}
                            className="flex justify-between py-2 text-sm">
                            <span>{h.hotelName}</span>
                            <span>{h.pendingBalance.toLocaleString()}원</span>
                        </div>

                    ))}
            </div>
            <a href="/admin/settlements"
                className="text-sm text-gray-500 mt-4 inline-flex items-center gap-1"
            >
                정산 관리로 이동 →
            </a>
        </div>
    )
}