import { searchInventorySummary } from "@/api/api"
import { useQuery } from "@tanstack/react-query"
import { adminInventoryKeys } from "./adminInventorykey"
import type { searchInventorySummaryRequest } from "@/type/hotel"

export const useInventorySummary = (filter: searchInventorySummaryRequest) => {

    const { data, isLoading, isError } = useQuery({
        queryKey: [adminInventoryKeys.list(filter)],
        queryFn: () => searchInventorySummary(filter).then(res => res.data)
    })

    return { data, isLoading, isError }
}