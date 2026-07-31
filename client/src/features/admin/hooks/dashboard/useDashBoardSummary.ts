import { getDashBoardSummaryInfo } from "@/api/api"
import { useQuery } from "@tanstack/react-query"

export const useDashBoardSummary = () => {
    const { data: summaryData, isLoading: isSummaryLoading, isError: isSummaryError } = useQuery({
        queryKey: [],
        queryFn: () => getDashBoardSummaryInfo().then(res => res.data),
        staleTime: 30_000,
        refetchInterval: 60_000
    })

    return { summaryData, isSummaryLoading, isSummaryError }
}