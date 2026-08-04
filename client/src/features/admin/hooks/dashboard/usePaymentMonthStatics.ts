import { getPaymentStatusByMonth } from "@/api/api"
import { useQuery } from "@tanstack/react-query"
import { adminDashboardKeys } from "./adminDashboardKeys"

export const usePaymentMonthStatistics = () => {


    const { data: paymentData, isLoading: isPaymentLoading, isError: isPaymentError } = useQuery({
        queryKey: adminDashboardKeys.paymentStatusMonth(),
        queryFn: () => getPaymentStatusByMonth().then(res => res.data)
    })

    return {
        paymentData, isPaymentError, isPaymentLoading
    }
}