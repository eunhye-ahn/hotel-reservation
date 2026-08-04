import { useReservationPayment } from "../hooks/useReservationPayment";
import { Spinner } from "@/common/component/Spinner";
import { ErrorMessage } from "@/common/component/ErrorMessage";

export const ReservationPage = () => {

    const { reservationKey, state, data, isLoading, isError, handlePayment } = useReservationPayment()

    if (isLoading) return <Spinner />
    if (isError) return <ErrorMessage />
    if (!reservationKey) return null;

    return (
        <div className="detail-container mt-10">
            {data?.availableCount == 0
                ? <p className="text-sm text-red-500 mb-4">이 가격의 객실이 마지막이에요 !</p>
                : <div className="text-sm text-red-500 mb-4">이 가격의 객실이 {data?.availableCount}개 남았어요</div>
            }

            <div className="flex gap-8">
                <div className="flex-1">
                    <div className="mb-4">
                        <p className="font-bold text-lg">{state.hotelName}</p>
                        <p className="text-sm text-gray-500">{state.hotelAddress}</p>
                    </div>

                    <p className="font-semibold text-sm mb-2">{state.roomTypeName}</p>

                    <div className="flex gap-4 mb-6">
                        <img
                            className="w-[200px] h-[150px] object-cover"
                            src={state.imageUrl}
                        />
                        <div className="flex items-end">
                            <p className="font-bold text-lg">{data?.totalPrice.toLocaleString()}원</p>
                        </div>
                    </div>

                    <div className="flex items-center gap-4 border border-gray-200 rounded-xl p-4">
                        <div className="flex-1 text-center">
                            <p className="text-xs text-gray-500 mb-1">체크인</p>
                            <p className="text-sm font-medium">{state.startDate}</p>
                            <p className="text-xs text-gray-500">{state.checkInTime?.substring(0, 5)}</p>
                        </div>
                        <div className="text-xs text-gray-400 shrink-0">1박</div>
                        <div className="flex-1 text-center">
                            <p className="text-xs text-gray-500 mb-1">체크아웃</p>
                            <p className="text-sm font-medium">{state.endDate}</p>
                            <p className="text-xs text-gray-500">{state.checkOutTime?.substring(0, 5)}</p>
                        </div>
                    </div>
                </div>

                <div className="w-[280px] shrink-0">
                    <div className="border border-gray-200 rounded-xl p-5 sticky top-6">
                        <div className="flex justify-between text-sm mb-4">
                            <span className="text-gray-500">결제금액</span>
                            <span className="font-bold">{data?.totalPrice.toLocaleString()}원</span>
                        </div>
                        <button
                            className="w-full bg-gray-900 text-white rounded-lg py-3 font-medium cursor-pointer hover:bg-gray-800"
                            onClick={handlePayment}
                        >
                            결제하기
                        </button>
                    </div>
                </div>
            </div>
        </div>
    )
}