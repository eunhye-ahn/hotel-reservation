export interface PaymentPrepareRequest {
    reservationKey: string,
    amount: number
}

export interface PaymentPrepareResponse {
    paymentOrderId: string,
    amount: number
}