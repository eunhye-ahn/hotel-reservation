export interface PaymentPrepareRequest {
    reservationKey: string,
    amount: number
}

export interface PaymentPrepareResponse {
    paymentOrderId: string,
    amount: number,
    userId: number
}

export interface PaymentConfirmRequest {
    paymentKey: string,
    orderId: string,
    amount: number
}
