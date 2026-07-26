import { getSettlements } from "@/api/api"
import type { AdminSettlementSearchRequest } from "@/type/admin"
import { useQuery } from "@tanstack/react-query"
import { adminSettlementKeys } from "./adminSettlementKeys"

export const useSettlementList = (filter: AdminSettlementSearchRequest) => {
    const { data, isLoading, isError } = useQuery({
        queryKey: adminSettlementKeys.list(filter),
        queryFn: () => getSettlements(filter).then(res => res.data)
    })

    return { data, isLoading, isError }
}

