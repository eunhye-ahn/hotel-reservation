import { getReservationStatus, confirmPayment } from "@/api/api";
import { getErrorCode, handleDefenseError } from "@/api/errorHelpers";
import { reservationKeys } from "@/features/mypage/hooks/reservationKeys";
import { useAuthStore } from "@/store/useAuthStore";
import { useQuery } from "@tanstack/react-query";
import { useEffect, useRef, useState } from "react";
import { useNavigate } from "react-router"
import { toast } from "react-toastify";

interface UsePaymentConfirmParams {
    orderId: string | null;
    paymentKey: string | null;
    amount: string | null;
}

const MAX_ATTEMPTS = 5;
const POLL_INTERVAL = 10_000;

export const usePaymentConfirm = ({ orderId, paymentKey, amount }: UsePaymentConfirmParams) => {
    const [confirmed, setConfirmed] = useState(false)
    const [isDelayed, setIsDelayed] = useState(false)
    const reservationKeyRef = useRef<string | null>(null)
    const navigate = useNavigate()
    const token = useAuthStore((state) => state.accessToken)
    const attemptRef = useRef(0);

    //결제상태 폴링
    const { data } = useQuery({
        queryKey: reservationKeys.paymentStatus(orderId!),
        queryFn: async () => {
            attemptRef.current += 1
            return getReservationStatus(reservationKeyRef.current!).then(res => res.data)
        },
        refetchInterval: (query) => {
            if (query.state.data === "PAID") return false
            if (attemptRef.current >= MAX_ATTEMPTS) return false
            return POLL_INTERVAL
        },
        enabled: confirmed && !!orderId && !isDelayed,
    })

    useEffect(() => {
        if (data === "PAID") {
            navigate(`/reservations/${reservationKeyRef.current}`)
            return
        }
        if (attemptRef.current >= MAX_ATTEMPTS) {
            setIsDelayed(true)
        }
    }, [data])



    //결제승인요청
    useEffect(() => {
        if (!token) return;
        if (!orderId || !paymentKey || !amount) return;

        confirmPayment({
            orderId,
            paymentKey,
            amount: Number(amount)
        })
            .then((res) => {
                reservationKeyRef.current = res.data.reservationKey;
                setConfirmed(true);
            }
            )
            .catch((error) => {
                const code = getErrorCode(error)
                if (code === "PAYMENT_AMOUNT_MISMATCH" || code === "PAYMENT_ALREADY_PROCESSED") {
                    handleDefenseError(error, () => { })
                    navigate(`/reservations/${reservationKeyRef.current ?? ""}`);
                    return
                }
                toast.error("결제 승인에 실패했습니다.");
                navigate("/");
            })
    }, [token])

    return { isDelayed }
}