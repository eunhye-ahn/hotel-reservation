import { useLocation, useNavigate, useParams } from "react-router"
import { useQuery } from "@tanstack/react-query";
import type { ReservationInfoResponse } from "@/type/reservation";
import { toast } from "react-toastify";
import { loadTossPayments } from "@tosspayments/tosspayments-sdk";
import { reservationInfo, preparePayment } from "@/api/api";
import { useRef } from "react";

export const ReservationPage = () => {
    const { reservationKey } = useParams<string>();
    const location = useLocation();
    const state = location.state;
    const navigate = useNavigate();

    const idempotencyKey = useRef<string>(crypto.randomUUID());

    const { data, isLoading, isError } = useQuery<ReservationInfoResponse>({
        queryKey: ["reservationInfo", reservationKey],
        queryFn: () => reservationInfo(reservationKey!).then((res) => res.data)
    });

    const handlePayment = async () => {
        try {
            //내 서버에서 paymentOrderId, amount 받아오기
            //오픈 -> 승인 결과 받은 후에 순서대로 실행되어야함
            const res = await preparePayment(reservationKey!, state.orderId, idempotencyKey.current);
            const { paymentOrderId, amount, userId } = res.data;

            //토스 결제창 오픈
            const tossPayments = await loadTossPayments(import.meta.env.VITE_TOSS_CLIENT_KEY);
            const payment = tossPayments.payment({ customerKey: `USER-${userId}` });

            await payment.requestPayment({
                method: "CARD",
                amount: {
                    currency: "KRW",
                    value: amount,
                },
                orderId: paymentOrderId,
                orderName: state.roomTypeName,
                successUrl: `${window.location.origin}/payments/success`,
                failUrl: `${window.location.origin}/payments/fail`,
            });

        } catch (err: any) {
            //사용자가 결제취소
            if (err.message === "취소되었습니다.") {
                return;
            }
            //타임아웃 시 (또는 네트워크오류?) 같은멱등키로 시도 로직 추가
            console.log(err)
            toast.error("결제 중 오류가 발생했습니다")
            navigate("/");
        }
    }

    if (isLoading) return <p>loading...</p>
    if (isError) {
        toast.error("일시적인 오류가 발생했습니다");
        navigate("/");
    }
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