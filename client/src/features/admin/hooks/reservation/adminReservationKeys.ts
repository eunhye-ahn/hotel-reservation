import type { AdminReseervationSearchRequest } from "@/type/admin";

export const adminReservationKeys = {
    all: ["admin-reservation"] as const,

    lists: () => [...adminReservationKeys.all, "list"] as const,
    list: (filter: AdminReseervationSearchRequest) => [...adminReservationKeys.lists(), filter] as const,

    details: () => [...adminReservationKeys.all, "detail"] as const,
    detail: (reservationId: number) => [...adminReservationKeys.details(), reservationId] as const,

    rooms: (reservationId: number) => [...adminReservationKeys.all, "rooms", reservationId] as const
}
