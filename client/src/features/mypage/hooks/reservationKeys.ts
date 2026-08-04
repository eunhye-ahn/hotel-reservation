import type { ReservationStatus } from "@/api/types/reservation";

export const reservationKeys = {
    all: ["reservations"] as const,

    myLists: () => [...reservationKeys.all, "myList"] as const,
    myList: (status: ReservationStatus) =>
        [...reservationKeys.myLists(), status] as const,

    info: (reservationKey: string) =>
        [...reservationKeys.all, "info", reservationKey] as const,

    confirm: (reservationKey: string) =>
        [...reservationKeys.all, "confirm", reservationKey] as const,

    paymentStatus: (orderId: string) =>
        [...reservationKeys.all, "paymentStatus", orderId] as const,
}