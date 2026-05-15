import type { PaymentPrepareResponse } from "@/shared/type/payment"
import { paymentApi } from "./axios/payment-axios"

export const preparePayment = (reservationKey: string) => {
    return paymentApi.post<PaymentPrepareResponse>(`/payments/prepare/${reservationKey}`)
}