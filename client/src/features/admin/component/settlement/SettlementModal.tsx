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
        <div className="mx-10 my-10">
            <strong>{hotelName}</strong> 
            <br></br>미정산 잔액: <strong>{pendingBalance.toLocaleString()}원</strong>
            <table className="w-full mt-3 text-sm border border-gray-200 overflow-hidden">
                <tbody>
                    <tr>
                        <th className="px-3 py-2 font-medium w-30%">정산 시작일</th>
                        <td className="px-3 py-2">
                            <input type="date" value={periodStart ?? ""}
                                onChange={(e) => setPeriodStart(e.target.value || undefined)} />
                        </td>
                    </tr>
                    <tr>
                        <th className="px-3 py-2 font-medium w-30%">정산 종료일</th>
                        <td className="px-3 py-2">
                            <input type="date" value={periodEnd ?? ""}
                                onChange={(e) => setPeriodEnd(e.target.value || undefined)} />
                        </td>
                    </tr>
                    <tr>
                       <th className="px-3 py-2 font-medium w-30%">예상 정산액</th>
                        <td className="px-3 py-2 text-right">
                            {isLoading ? <Spinner />
                                : isError ? <ErrorMessage />
                                    : (<p><strong>{data?.toLocaleString()}원</strong></p>)
                            }
                        </td>
                    </tr>
                    <tr>
                        <td className="px-3 py-2 text-center " colSpan={2}>
                            <button onClick={handleExecute}
                                className="px-4 py-2 text-sm bg-gray-900 text-white cursor-pointer hover:bg-gray-500"
                                disabled={isExecuting || !periodStart || !periodEnd}>
                                {isExecuting ? "Loading..." : "정산하기"}
                            </button>
                        </td>
                    </tr>
                </tbody>
            </table>
        </div>
    )
}
