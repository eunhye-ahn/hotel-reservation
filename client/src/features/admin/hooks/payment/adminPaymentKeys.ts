import type { AdminPaymentSearchRequest } from "@/api/types/admin";

export const adminPaymentKeys = {
    all: ["adminPayments"] as const,
    lists: () => [...adminPaymentKeys.all, "list"] as const,
    list: (filter: AdminPaymentSearchRequest) => [...adminPaymentKeys.lists(), filter] as const,
}