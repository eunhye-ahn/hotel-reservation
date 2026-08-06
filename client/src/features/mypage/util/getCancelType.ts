export const getCancelType = (cancelType: string) => {
    if (cancelType === "USER") return "사용자 취소";
    if (cancelType === "ADMIN") return "관리자 취소";
};