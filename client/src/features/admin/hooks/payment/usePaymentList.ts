import { getPayments } from "@/api/api"
import type { AdminPaymentSearchRequest } from "@/api/types/admin"
import { useQuery } from "@tanstack/react-query"
import { adminPaymentKeys } from "./adminPaymentKeys"

export const usePaymentList = (filter: AdminPaymentSearchRequest) => {
    const { data, isLoading, isError } = useQuery({
        queryKey: adminPaymentKeys.list(filter),
        queryFn: () => getPayments(filter).then(res => res.data)
    })

    return { data, isLoading, isError }
}