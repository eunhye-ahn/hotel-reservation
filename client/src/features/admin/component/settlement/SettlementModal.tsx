import { addDays, format, subDays } from "date-fns"
import { useRef, useState } from "react"
import { useExecuteSettlement } from "../../hooks/settlement/useExecuteSettlement"
import { useSettlementPreview } from "../../hooks/settlement/useSettlementPreview"
import { Spinner } from "@/common/component/Spinner"
import { ErrorMessage } from "@/common/component/ErrorMessage"
import { useNavigate } from "react-router"

interface SettlementModalProps {
    hotelId: number,
    hotelName: string,
    pendingBalance: number,
    lastSettledAt?: string,
    onSuccess: () => void
}

export const SettlementModal = ({ hotelId, hotelName, pendingBalance, lastSettledAt, onSuccess }: SettlementModalProps) => {
    const navigate = useNavigate()
    const defaultPeriodStart = lastSettledAt ? format(addDays(new Date(lastSettledAt), 1), 'yyyy-MM-dd')
        : format(subDays(new Date(), 7), 'yyyy-MM-dd');

    const [periodStart, setPeriodStart] = useState<string | undefined>(defaultPeriodStart)
    const [periodEnd, setPeriodEnd] = useState<string | undefined>(format(new Date(), 'yyyy-MM-dd'))

    const { data, isLoading, isError } = useSettlementPreview({ hotelId, periodStart, periodEnd })

    const settlementKey = useRef(crypto.randomUUID())
    const { executeSettleMutate, isExecuting } = useExecuteSettlement({
        hotelId,
        settlementKey: settlementKey.current,
        periodStart: periodStart!,
        periodEnd: periodEnd!
    })

    const handleExecute = () => {
        executeSettleMutate(undefined, {
            onSuccess: () => {
                onSuccess()
                navigate("/admin/settlements")
            }
        })
    }

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
            <button onClick={handleExecute}
                disabled={isExecuting || !periodStart || !periodEnd}>
                {isExecuting ? "Loading..." : "정산하기"}
            </button>
        </div>
    )
}
