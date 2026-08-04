import { getDailyStatisticsInfo } from "@/api/api"
import { useQuery } from "@tanstack/react-query"
import { adminDashboardKeys } from "./adminDashboardKeys"

export const useDailyStatistics = () => {
    const { data: statisticsData, isLoading: isStatisticsLoading, isError: isStatisticsError } = useQuery({
        queryKey: adminDashboardKeys.dailyStatistics(),
        queryFn: () => getDailyStatisticsInfo().then(res => res.data)
    })

    return { statisticsData, isStatisticsError, isStatisticsLoading }
}