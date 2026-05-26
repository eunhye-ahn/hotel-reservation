import { orderApi } from "./axios/order-axios"

export const createOrder = () => {
    return orderApi.post<string>("/orders")
}