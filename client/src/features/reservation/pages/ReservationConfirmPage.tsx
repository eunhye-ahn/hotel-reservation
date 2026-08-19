
import { useNavigate, useParams } from "react-router"
import dayjs from 'dayjs';
import { toast } from "react-toastify";
import { getErrorMessage } from '@/api/errorHelpers';
import { useReservationConfirm } from '../hooks/useReservationConfirm';
import { Spinner } from "@/common/component/Spinner";
import { getPaymentStatus } from "@/features/mypage/util/getPaymentStatus";
import { getCancelType } from "@/features/mypage/util/getCancelType";
import { PrevBtn } from "@/common/component/PrevBtn"

export const ReservationConfirmPage = () => {
    const { reservationKey } = useParams();
    if (!reservationKey) return;
    const navigate = useNavigate()

    const { data, error, isLoading, isError } = useReservationConfirm({ reservationKey })


    if (isLoading) return <Spinner />
    if (isError) {
        toast.error(getErrorMessage(error))
        navigate("/mypage")
        return null;
    }

    const numberOfNights = dayjs(data?.endDate).diff(dayjs(data?.startDate), 'day');

    return (
        <div className="detail-container">
            <PrevBtn />
            <div className="text-xl font-bold mb-6 mt-10">예약 확인서</div>

            <div className="mb-5">
                <div className="flex justify-between text-sm py-1">
                    <span className="text-gray-500">예약번호</span>
                    <span>{data?.reservationKey}</span>
                </div>
                <div className="flex justify-between text-sm py-1">
                    <span className="text-gray-500">거래 일시</span>
                    <span>{data?.createdAt}</span>
                </div>
            </div>

            <div className="font-semibold text-sm mb-2">상품 및 이용정보</div>
            <hr className="border-gray-200 mb-4" />

            <div className="flex items-center gap-4 mb-4">
                <img
                    className="w-[200px] h-[150px] object-cover"
                    src={data?.roomTypeImageUrl}
                />
                <div className="flex-1">
                    <p className="font-semibold text-sm">{data?.hotelName}</p>
                    <p className="text-xs text-gray-500 mt-1">{data?.roomTypeName}</p>
                </div>
                <div className="flex flex-col">
                    <span className="text-center text-xs px-3 py-1.5 rounded-lg bg-gray-900 text-white">
                        {data?.reservationStatus === "CANCELED" ?
                            getCancelType(data.cancelType)
                            : getPaymentStatus(data?.paymentStatus)}
                    </span>
                    <p className="text-xs text-gray-500 mb-8 text-right mt-2">
                        {data?.reservationStatus === "CANCELED" && data.cancelReason != null && '취소사유 : ' + data.cancelReason}
                    </p>
                </div>
            </div>

            <div className="text-xs text-gray-500 mb-8 text-right">
                <p>{data?.startDate} ~{data?.endDate} | {numberOfNights}박</p>
                <p className="mt-1">체크인 {data?.checkInTime?.substring(0, 5)} | 체크아웃 {data?.checkOutTime?.substring(0, 5)}</p>
            </div>

            <div className="font-semibold text-sm mb-2">결제상세</div>
            <hr className="border-gray-200 mb-4" />

            <div>
                <div className="flex justify-between text-sm py-1">
                    <span></span>
                    <span className="text-gray-500">X {numberOfNights}박</span>
                </div>
                <div className="flex justify-between text-sm py-1">
                    <span></span>
                    <span className="text-gray-500">X 객실 {data?.numberOfRooms}개</span>
                </div>
                <hr className="border-gray-200 my-3" />
                <div className="flex justify-between font-bold text-base">
                    <span>총 결제금액</span>
                    <span>{data?.totalPrice.toLocaleString()}</span>
                </div>
            </div>

        </div>
    )
}