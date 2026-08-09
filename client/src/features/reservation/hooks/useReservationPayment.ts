import { preparePayment, reservationInfo } from "@/api/api";
import { getErrorCode, handleDefenseError } from "@/api/errorHelpers";
import type { ReservationInfoResponse } from "@/api/types/reservation";
import { reservationKeys } from "@/features/mypage/hooks/reservationKeys";
import { useQuery } from "@tanstack/react-query";
import { loadTossPayments } from "@tosspayments/tosspayments-sdk";
import { useRef } from "react";
import { useLocation, useNavigate, useParams } from "react-router";
import { toast } from "react-toastify";

export const useReservationPayment = () => {

    const { reservationKey } = useParams<string>();
    const location = useLocation();
    const state = location.state;
    const navigate = useNavigate();

    const paymentKey = useRef<string>(crypto.randomUUID());

    const { data, isLoading, isError } = useQuery<ReservationInfoResponse>({
        queryKey: reservationKeys.info(reservationKey!),
        queryFn: () => reservationInfo(reservationKey!).then((res) => res.data)
    });

    const handlePayment = async () => {
        try {
            //내 서버에서 paymentOrderId, amount 받아오기
            //오픈 -> 승인 결과 받은 후에 순서대로 실행되어야함
            const res = await preparePayment(reservationKey!, state.orderId, paymentKey.current);
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
            if (err?.code === "USER_CANCEL") return;

            //서버에러
            const code = getErrorCode(err)
            //사용자가 결제취소
            if (code === "PAYMENT_ALREADY_PROCESSED" || code === "MISSING_IDEMPOTENCY_KEY") {
                handleDefenseError(err, () => { })
                navigate(`/reservations/${reservationKey}`)
                return
            }
            toast.error("결제 중 오류가 발생했습니다")
            navigate("/");
        }
    }

    return { reservationKey, state, data, isLoading, isError, handlePayment }
}