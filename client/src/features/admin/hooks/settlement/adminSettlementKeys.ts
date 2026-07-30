import type { AdminSettlementSearchRequest, SettlementHistorySearchRequest } from "@/type/admin";

export const adminSettlementKeys = {
    all: ["admin-settlement"] as const,

    lists: () => [...adminSettlementKeys.all, "list"] as const,
    list: (filter: AdminSettlementSearchRequest) => [...adminSettlementKeys.lists(), filter] as const,

    details: () => [...adminSettlementKeys.all, "detail"] as const,
    detail: (hotelId: number, filter: SettlementHistorySearchRequest) =>
        [...adminSettlementKeys.details(), hotelId, filter] as const,


    preview: (hotelId: number, periodStart?: string, periodEnd?: string) =>
        [...adminSettlementKeys.all, "preview", hotelId, periodStart, periodEnd] as const,
}