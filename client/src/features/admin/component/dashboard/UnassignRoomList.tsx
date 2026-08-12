import { useNavigate } from "react-router"
import { useUnassignRoomQuery } from "../../hooks/dashboard/useUnassignRoomQuery"
import { Spinner } from "@/common/component/Spinner"
import { ErrorMessage } from "@/common/component/ErrorMessage"

export const UnassginRoomList = () => {
    const { unassignRoomData, isUnassignRoomError, isUnassignRoomLoading } = useUnassignRoomQuery()
    const navigate = useNavigate()
    
    if(isUnassignRoomError) return <Spinner/>
    if(isUnassignRoomLoading) return <ErrorMessage/>

    return (
        <div>
            <div className="flex items-center justify-between px-5 py-3 bg-gray-50 border-b border-gray-200">
                <span className="font-semibold text-sm">조치 필요 - 미배정 예약</span>
            </div>
            <div className="divide-y divide-gray-500">
                {unassignRoomData?.length === 0 ? <p className="text-xs text-center mt-5">미배정된 예약이 없습니다</p>
                    : unassignRoomData?.map(r => (
                        <div key={r.reservationId} className="flex items-center justify-between py-3">
                            <div onClick={() => navigate(`/admin/reservations/${r.reservationId}`)}>
                                <p className="text-sm font-medium underline">
                                    {r.displayReservationNo}
                                </p>
                                <p className="text-sm text-gray-500">
                                    {r.hotelName} {r.startDate}
                                </p>
                            </div>
                            <span className="text-sm text-red-500">미배정</span>
                        </div>
                    ))}
            </div>
            <a href="/admin/reservations"
                className="text-sm text-gray-500 mt-4 inline-flex items-center gap-1"
            >
                전체 보기 →
            </a>
        </div >
    )
}