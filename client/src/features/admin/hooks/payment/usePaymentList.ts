import { getPayments } from "@/api/api"
import type { AdminPaymentSearchRequest } from "@/type/admin"
import { useQuery } from "@tanstack/react-query"

export const usePaymentList = (filter: AdminPaymentSearchRequest) => {
    const { data, isLoading, isError } = useQuery({
        queryKey: ["paymentlist", filter],
        queryFn: () => getPayments(filter).then(res => res.data)
    })

    return { data, isLoading, isError }
}