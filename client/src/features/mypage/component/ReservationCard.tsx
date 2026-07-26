import type { ReservationResponse, ReservationStatus } from "@/type/reservation";
import { getPaymentStatus } from "../util/getPaymentStatus";

interface ReservationCardProps {
    isPending: boolean,
    status: ReservationStatus,
    reservation: ReservationResponse,
    onDetailClick: () => void,
    onCancelClick: () => void
}

export const ReservationCard = ({ reservation, isPending, status, onDetailClick, onCancelClick }: ReservationCardProps) => {
    return (
        <div className="reservation-card" key={reservation.reservationKey}>
            <div className="reservation-card-header">
                <span className="reservation-card-status">{status === 'AFTER_USE' ? '이용완료' : status === 'BEFORE_USE' ? '이용전' : '취소'}</span>
                <span>{getPaymentStatus(reservation.paymentStatus)}</span>
                <div className="reservation-card-btns">
                    <button className="reservation-detail-btn" onClick={onDetailClick}>상세보기</button>
                    <button className="reservation-cancel-btn" onClick={onCancelClick} disabled={isPending}>
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
    )
}