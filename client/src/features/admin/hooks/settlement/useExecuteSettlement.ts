import { executeSettlementByAdmin } from "@/api/api"
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { adminSettlementKeys } from "./adminSettlementKeys"
import { adminDashboardKeys } from "../dashboard/adminDashboardKeys"

interface UseExecuteSettlementProps {
    hotelId: number,
    periodStart: string,
    periodEnd: string,
    settlementKey: string
}

export const useExecuteSettlement = ({ hotelId, settlementKey, periodStart, periodEnd }: UseExecuteSettlementProps) => {
    const queryClient = useQueryClient()

    const { mutate: executeSettleMutate, isPending: isExecuting } = useMutation({
        mutationFn: () => executeSettlementByAdmin(hotelId, settlementKey, periodStart, periodEnd),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: adminSettlementKeys.lists() }),
                queryClient.invalidateQueries({ queryKey: adminDashboardKeys.pendingHotels() })
        }
    })

    return { executeSettleMutate, isExecuting }
}

