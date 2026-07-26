import type { AdminSettlementSearchRequest } from "@/type/admin";

export const adminSettlementKeys = {
    all: ["admin-settlement"] as const,

    lists: () => [...adminSettlementKeys.all, "list"] as const,
    list: (filter: AdminSettlementSearchRequest) => [...adminSettlementKeys.lists(), filter] as const,

    preview: (hotelId: number, periodStart?: string, periodEnd?: string) =>
        [...adminSettlementKeys.all, "preview", hotelId, periodStart, periodEnd] as const,
}