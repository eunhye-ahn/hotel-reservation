import { executeSettlementByAdmin, previewSettlementAmount } from "@/api/api"
import { getToday } from "@/component/common/util/date"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import { addDays, format, subDays } from "date-fns"
import { useState } from "react"

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
    const [periodEnd, setPeriodEnd] = useState<string | undefined>(getToday())
    const queryClient = useQueryClient()

    const { data, isLoading, isError } = useQuery({
        queryKey: ["settlement-hotel", hotelId, periodStart, periodEnd],
        queryFn: () => previewSettlementAmount(hotelId, periodStart, periodEnd)
            .then(res => res.data),
        enabled: !!periodStart && !!periodEnd
    })


    const { mutate: executeSettleMutate, isPending: isExecuting } = useMutation({
        mutationFn: () => executeSettlementByAdmin(hotelId, periodStart!, periodEnd!),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: ["settlement-hotel"] })
            queryClient.invalidateQueries({ queryKey: ["settlement"] })


        }
    })

    console.log(data)

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
            <p>예상정산액 : {data?.toLocaleString()}원</p>

            <button onClick={() => executeSettleMutate()}
                disabled={isExecuting || !periodStart || !periodEnd}>
                정산하기
            </button>
        </div>
    )
}
