import type { PaymentConfirmRequest, PaymentConfirmResponse, PaymentPrepareResponse } from "@/shared/type/payment"
import { paymentApi } from "./axios/payment-axios"

export const preparePayment = (reservationKey: string) => {
    return paymentApi.post<PaymentPrepareResponse>(`/payments/prepare/${reservationKey}`)
}

//결제승인처리api
export const confirmPayment = (request: PaymentConfirmRequest) => {
    return paymentApi.post<PaymentConfirmResponse>("/payments/confirm", request)
}