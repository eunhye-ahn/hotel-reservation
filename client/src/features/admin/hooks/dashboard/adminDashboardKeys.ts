
export const adminDashboardKeys = {
    all: ["adminDashboard"] as const,
    summary: () => [...adminDashboardKeys.all, "summary"] as const,
    dailyStatistics: () => [...adminDashboardKeys.all, "daily-statistics"] as const,
    unAssignRoom: () => [...adminDashboardKeys.all, "unassign-room"] as const,
    pendingHotels: () => [...adminDashboardKeys.all, "pendingHotels"] as const,
    reservationStatusMonth: () => [...adminDashboardKeys.all, "reservationStatusMonth"] as const,
    paymentStatusMonth: () => [...adminDashboardKeys.all, "paymentStatusMonth"] as const,
}