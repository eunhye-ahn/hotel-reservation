import { useNavigate, useSearchParams } from "react-router";
import { usePaymentConfirm } from "../hooks/usePaymentConfirm";

export default function PaymentSuccess() {
    const [searchParams] = useSearchParams();
    const orderId = searchParams.get("orderId")
    const paymentKey = searchParams.get("paymentKey")
    const amount = searchParams.get("amount")
    const navigate = useNavigate()

    const { isDelayed } = usePaymentConfirm({ orderId, paymentKey, amount })

    if (isDelayed) {
        return (
            <div className="flex flex-col items-center justify-center h-screen gap-3">
                <p className="font-semibold">확인이 지연되고 있습니다</p>
                <p className="text-sm text-gray-500">
                    잠시 후 예약 내역에서 다시 확인해주세요. 계속되면 고객센터로 문의해주세요.
                </p>
                <button
                    className="px-4 py-2 bg-gray-900 text-white rounded-lg text-sm"
                    onClick={() => navigate("/mypage")}>
                    마이페이지로 이동
                </button>
            </div>
        )
    }
    return (
        <div className="flex flex-col items-center justify-center h-screen gap-3">
            <p className="font-semibold">결제 확인 중입니다</p>
        </div>
    )
}