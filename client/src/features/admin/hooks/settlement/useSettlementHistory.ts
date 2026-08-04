import { getSettlementByHotel } from "@/api/api"
import type { SettlementHistorySearchRequest } from "@/api/types/admin"
import { useQuery } from "@tanstack/react-query"
import { adminSettlementKeys } from "./adminSettlementKeys"


export const useSettlementHistory = (hotelId: number, filter: SettlementHistorySearchRequest) => {

    const { data, isLoading, isError } = useQuery({
        queryKey: adminSettlementKeys.detail(hotelId, filter),
        queryFn: () => getSettlementByHotel(
            hotelId,
            {
                startDate: filter.startDate,
                endDate: filter.endDate,
                status: filter.status,
                page: filter.page
            })
            .then(res => res.data)
    })

    return { data, isLoading, isError }
}