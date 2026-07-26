import { getSettlements } from "@/api/api"
import type { AdminSettlementSearchRequest } from "@/type/admin"
import { useQuery } from "@tanstack/react-query"

export const useSettlementList = (filter: AdminSettlementSearchRequest) => {
    const { data, isLoading, isError } = useQuery({
        queryKey: ["settlement", filter],
        queryFn: () => getSettlements(filter).then(res => res.data)
    })

    return { data, isLoading, isError }
}

