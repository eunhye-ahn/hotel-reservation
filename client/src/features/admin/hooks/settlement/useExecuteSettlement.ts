import { executeSettlementByAdmin } from "@/api/api"
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { adminSettlementKeys } from "./adminSettlementKeys"

interface UseExecuteSettlementProps {
    hotelId: number,
    periodStart: string,
    periodEnd: string
}

export const useExecuteSettlement = ({ hotelId, periodStart, periodEnd }: UseExecuteSettlementProps) => {
    const queryClient = useQueryClient()

    const { mutate: executeSettleMutate, isPending: isExecuting } = useMutation({
        mutationFn: () => executeSettlementByAdmin(hotelId, periodStart, periodEnd),
        onSuccess: () => {
            queryClient.invalidateQueries({ queryKey: adminSettlementKeys.lists() })
        }
    })

    return { executeSettleMutate, isExecuting }
}

