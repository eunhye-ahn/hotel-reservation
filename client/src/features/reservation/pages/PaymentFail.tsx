import { useNavigate, useSearchParams } from "react-router";

export default function PaymentFail() {
    const [searchParams] = useSearchParams()
    const navigate = useNavigate()

    const code = searchParams.get("code")
    const message = searchParams.get("message")

    return (
        <div className="flex flex-col items-center justify-center h-screen gap-3">
            <h2 className="text-lg font-semibold">결제에 실패했습니다</h2>
            <p className="text-sm text-gray-600">{message ?? "결제 처리 중 문제가 발생했습니다"}</p>
            {code && <p className="text-xs text-gray-400">에러코드: {code}</p>}
            <button
                className="mt-2 px-4 py-2 bg-gray-900 text-white rounded-lg text-sm"
                onClick={() => navigate(-2)}
            >
                다시 시도하기
            </button>
        </div>
    )
}