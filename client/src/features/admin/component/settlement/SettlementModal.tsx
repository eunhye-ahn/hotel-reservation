import { addDays, format, subDays } from "date-fns"
import { useState } from "react"
import { useExecuteSettlement } from "../../hooks/settlement/useExecuteSettlement"
import { useSettlementPreview } from "../../hooks/settlement/useSettlementPreview"
import { Spinner } from "@/common/component/Spinner"
import { ErrorMessage } from "@/common/component/ErrorMessage"

interface SettlementModalProps {
    hotelId: number,
    hotelName: string,
    pendingBalance: number,
    lastSettledAt?: string
    onClose: () => void
}

export const SettlementModal = ({ hotelId, hotelName, pendingBalance, lastSettledAt, onClose }: SettlementModalProps) => {
    const defaultPeriodStart = lastSettledAt ? format(addDays(new Date(lastSettledAt), 1), 'yyyy-MM-dd')
        : format(subDays(new Date(), 7), 'yyyy-MM-dd');

    const [periodStart, setPeriodStart] = useState<string | undefined>(defaultPeriodStart)
    const [periodEnd, setPeriodEnd] = useState<string | undefined>(format(new Date(), 'yyyy-MM-dd'))

    const { data, isLoading, isError } = useSettlementPreview({ hotelId, periodStart, periodEnd })
    const { executeSettleMutate, isExecuting } = useExecuteSettlement({ hotelId, periodStart: periodStart!, periodEnd: periodEnd! })


    return (
        <div>
            <p>{hotelName} - 미정산 잔액: {pendingBalance.toLocaleString()}원</p>

            <div>
                <label>정산 시작일</label>
                <input type="date" value={periodStart ?? ""}
                    onChange={(e) => setPeriodStart(e.target.value || undefined)} />
            </div>
            <div>
                <label>정산 종료일</label>
                <input type="date" value={periodEnd ?? ""}
                    onChange={(e) => setPeriodEnd(e.target.value || undefined)} />
            </div>
            {isLoading ? <Spinner />
                : isError ? <ErrorMessage />
                    : (
                        <p>예상정산액 : {data?.toLocaleString()}원</p>
                    )}
            <button onClick={() => executeSettleMutate()}
                disabled={isExecuting || !periodStart || !periodEnd}>
                정산하기
            </button>
        </div>
    )
}
