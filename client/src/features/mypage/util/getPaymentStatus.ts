export const getPaymentStatus = (status: string) => {
    if (status === "PENDING") return "결제미완료";
    if (status === "PAID") return "결제완료";
    if (status === "EXPIRED") return "예약만료";
};