import type { ReservationResponse, ReservationStatus } from "@/api/types/reservation";
import { getPaymentStatus } from "../util/getPaymentStatus";
import { Spinner } from "@/common/component/Spinner";

interface ReservationCardProps {
    isPending: boolean,
    status: ReservationStatus,
    reservation: ReservationResponse,
    onDetailClick: () => void,
    onCancelClick: () => void
}

export const ReservationCard = ({ reservation, isPending, status, onDetailClick, onCancelClick }: ReservationCardProps) => {
    return (
        <div className="border border-gray-200 overflow-hidden">
            {/* 상단: 상태 + 버튼 */}
            <div className="flex items-center justify-between px-4 py-3 border-b border-gray-100 bg-gray-50">
                <div className="flex items-center gap-2">
                    <span className="text-sm font-semibold">
                        {status === 'AFTER_USE' ? '이용완료' : status === 'BEFORE_USE' ? '이용전' : '취소'}
                    </span>
                    <span className="text-xs text-gray-500">{getPaymentStatus(reservation.paymentStatus)}</span>
                </div>
                <div className="flex gap-2">
                    <button
                        className="px-3 py-1.5 text-xs border border-gray-300 rounded-full cursor-pointer hover:bg-gray-100"
                        onClick={onDetailClick}
                    >
                        상세보기
                    </button>
                    {reservation.paymentStatus != "PAID" &&
                        <button
                            className="px-3 py-1.5 text-xs text-red-500 border border-red-500 rounded-full cursor-pointer hover:font-bold disabled:opacity-40 disabled:cursor-not-allowed"
                            onClick={onCancelClick}
                            disabled={isPending}
                        >
                            {isPending ? <Spinner /> : "예약취소"}
                        </button>
                    }
                </div>
            </div>

            {/* 하단: 이미지 + 호텔정보 + 날짜 */}
            <div className="flex gap-4 p-4">
                <img
                    className="w-[200px] h-[150px] object-cover"
                    src={reservation.hotelImageUrl}
                />
                <div className="flex-1">
                    <p className="font-semibold text-sm">{reservation.hotelName}</p>
                    <p className="text-xs text-gray-500 mt-1">{reservation.roomTypeName} &nbsp; 1박</p>
                </div>
                <div className="text-right text-xs text-gray-500 ">
                    <p>{reservation.startDate}~{reservation.endDate} | 1박</p>
                    <p className="mt-1">
                        체크인 {reservation.checkInTime.substring(0, 5)} | 체크아웃 {reservation.checkOutTime.substring(0, 5)}
                    </p>
                </div>
            </div>
        </div>
    )
}