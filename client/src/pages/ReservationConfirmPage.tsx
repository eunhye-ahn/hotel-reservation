
import type { ReservationDetailResponse } from "@/type/reservation";
import { useNavigate, useParams } from "react-router"
import dayjs from 'dayjs';
import { useQuery } from "@tanstack/react-query";
import NotFoundPage from "./NotFoundPage";
import { toast } from "react-toastify";
import { reservationConfirm } from "@/api/api";

export const ReservationConfirmPage = () => {
    const { reservationKey } = useParams();
    const navigate = useNavigate();
    if (!reservationKey) return;

    const { data, isLoading, isError, error } = useQuery<ReservationDetailResponse>({
        queryKey: ["reservationConfirm", reservationKey],
        queryFn: () => reservationConfirm(reservationKey).then((res) => res.data)
    })

    if (isLoading) return <p>Loading...</p>
    if (isError) {
        const code = (error as any).response.data.code
        if (code === "RESERVATION_NOT_FOUND") {
            return <NotFoundPage />
        }
        toast.error("일시적인 오류가 발생했습니다")
        navigate(-1)
        return null;
    }

    const numberOfNights = dayjs(data?.endDate).diff(dayjs(data?.startDate), 'day');

    const getPaymentStatus = (status: string | undefined) => {
        if (status == "PENDING") {
            return "결제미완료"
        }
        else if (status == "PAID") {
            return "결제완료"
        }
    }



    return (
        <div className="detail-container">
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
                <span className="text-xs px-3 py-1.5 rounded-lg bg-gray-900 text-white">
                    {getPaymentStatus(data?.status)}
                </span>
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