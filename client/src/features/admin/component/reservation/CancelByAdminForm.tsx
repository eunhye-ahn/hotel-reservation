import { useState } from "react"
import { useCancelByAdmin } from "../../hooks/reservation/useCancelByAdmin"

interface CancelByAdminFormProps {
    reservationId: number,
    paymentStatus: string,
    refundPrice: number
}


export const CancelByAdminForm = ({ reservationId, paymentStatus, refundPrice }: CancelByAdminFormProps) => {

    const [customReason, setCustomReason] = useState<string>("")
    const [cancelReasonType, setCancelReasonType] = useState<string>("")

    const { cancelByAdminMutate, isCanceling } = useCancelByAdmin(reservationId)

    const cancelReason = cancelReasonType === "기타" ? customReason : cancelReasonType

    return (
        <div>
            <table className="w-full text-sm">
                <tbody className="divide-y divide-gray-100">
                    <tr>
                        <th className="w-28 text-left px-4 py-3 bg-gray-50 font-medium text-gray-500 align-top">
                            사유
                        </th>
                        <td className="px-4 py-3">
                            <div className="flex flex-col gap-2">
                                <select
                                    value={cancelReasonType}
                                    onChange={(e) => setCancelReasonType(e.target.value)}
                                    className="border border-gray-300 rounded-lg px-3 py-2 text-sm w-full max-w-xs">
                                    <option value="">사유 선택</option>
                                    <option value="고객요청">고객요청</option>
                                    <option value="재고오류">재고오류</option>
                                    <option value="결제오류">결제오류</option>
                                    <option value="기타">기타</option>
                                </select>
                            </div>
                            {cancelReasonType === "기타" && (
                                <input
                                    type="text"
                                    value={customReason}
                                    onChange={(e) => setCustomReason(e.target.value)}
                                    placeholder="사유를 입력하세요"
                                    className="border border-gray-300 rounded-lg px-3 py-2 text-sm w-full max-w-xs"
                                />
                            )}
                        </td>
                        {paymentStatus === "PAID" &&
                            <>
                                <th>환불액</th>
                                <td>{refundPrice.toLocaleString()}원</td>
                            </>
                        }
                    </tr>
                    <tr>
                        <td colSpan={2}>
                            <button onClick={() => cancelByAdminMutate(cancelReason)}
                                disabled={isCanceling || !cancelReason}
                                className="px-4 py-2 text-sm text-white bg-red-500 cursor-pointer hover:bg-red-600 disabled:opacity-40 disabled:cursor-not-allowed">
                                {isCanceling ? "처리 중..." : "예약취소"}
                            </button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    )
}