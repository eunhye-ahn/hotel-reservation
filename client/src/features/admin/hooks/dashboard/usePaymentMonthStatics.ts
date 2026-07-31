import { getPaymentStatusByMonth } from "@/api/api"
import { useQuery } from "@tanstack/react-query"

export const usePaymentMonthStatistics = () => {


    const { data: paymentData, isLoading: isPaymentLoading, isError: isPaymentError } = useQuery({
        queryKey: ["pay"],
        queryFn: () => getPaymentStatusByMonth().then(res => res.data)
    })

    return {
        paymentData, isPaymentError, isPaymentLoading
    }
}