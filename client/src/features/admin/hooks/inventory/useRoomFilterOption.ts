import { useQuery } from "@tanstack/react-query"
import { adminInventoryKeys } from "./adminInventorykey"
import { getFilterOptions } from "@/api/api"

export const useRoomFilterOptions = (hotelId: number) => {
    const { data: optionData, isLoading: isOptionLoading, isError: isOptionError } = useQuery({
        queryKey: adminInventoryKeys.option(hotelId),
        queryFn: () => getFilterOptions(hotelId).then(res => res.data),
        enabled: !!hotelId
    })

    return { optionData, isOptionLoading, isOptionError }
}