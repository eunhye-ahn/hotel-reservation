import type { AdminReseervationSearchRequest } from "@/api/types/admin";

export const adminReservationKeys = {
    all: ["admin-reservation"] as const,

    lists: () => [...adminReservationKeys.all, "list"] as const,
    list: (filter: AdminReseervationSearchRequest) => [...adminReservationKeys.lists(), filter] as const,

    details: () => [...adminReservationKeys.all, "detail"] as const,
    detail: (reservationId: number) => [...adminReservationKeys.details(), reservationId] as const,

    rooms: () => [...adminReservationKeys.all, "room"] as const,
    room: (reservationId: number) => [...adminReservationKeys.all, "rooms", reservationId] as const
}
