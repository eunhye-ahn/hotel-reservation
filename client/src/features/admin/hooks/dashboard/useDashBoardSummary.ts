import { getDashBoardSummaryInfo } from "@/api/api"
import { useQuery } from "@tanstack/react-query"
import { adminDashboardKeys } from "./adminDashboardKeys"

export const useDashBoardSummary = () => {
    const { data: summaryData, isLoading: isSummaryLoading, isError: isSummaryError } = useQuery({
        queryKey: adminDashboardKeys.summary(),
        queryFn: () => getDashBoardSummaryInfo().then(res => res.data),
        staleTime: 30_000,
        refetchInterval: 60_000
    })

    return { summaryData, isSummaryLoading, isSummaryError }
}