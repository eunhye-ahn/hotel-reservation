import { useState } from "react"
import { useCancelByAdmin } from "../../hooks/reservation/useCancelByAdmin"

interface CancelByAdminFormProps {
    reservationId: number,
    refundPrice: number
}


export const CancelByAdminForm = ({ reservationId, refundPrice }: CancelByAdminFormProps) => {

    const [customReason, setCustomReason] = useState<string>("")
    const [cancelReasonType, setCancelReasonType] = useState<string>("")

    const { cancelByAdminMutate, isCanceling } = useCancelByAdmin(reservationId)

    const cancelReason = cancelReasonType === "기타" ? customReason : cancelReasonType

    return (
        <div>
            <table>
                <tbody>
                    <tr>
                        <th>사유</th>
                        <td>
                            <select value={cancelReasonType} onChange={(e) => setCancelReasonType(e.target.value)}>
                                <option value="">사유 선택</option>
                                <option value="고객요청">고객요청</option>
                                <option value="재고오류">재고오류</option>
                                <option value="결제오류">결제오류</option>
                                <option value="기타">기타</option>
                            </select>
                            {cancelReasonType === "기타" && (
                                <input
                                    type="text"
                                    value={customReason}
                                    onChange={(e) => setCustomReason(e.target.value)}
                                    placeholder="사유를 입력하세요"
                                />
                            )}
                        </td>
                        <th>환불액</th>
                        <td>{refundPrice}원</td>
                    </tr>
                    <tr>
                        <td colSpan={2}>
                            <button onClick={() => cancelByAdminMutate(cancelReason)} disabled={isCanceling || !cancelReason}>예약취소</button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    )
}