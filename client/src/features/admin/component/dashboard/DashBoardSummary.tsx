import { Spinner } from "@/common/component/Spinner"
import { useDashBoardSummary } from "../../hooks/dashboard/useDashBoardSummary"
import { ErrorMessage } from "@/common/component/ErrorMessage"

export const DashBoardSummary = () => {
    const { summaryData, isSummaryError, isSummaryLoading } = useDashBoardSummary()

    if (isSummaryLoading) return <Spinner />
    if (isSummaryError) return <ErrorMessage />

    const diff = summaryData?.checkInDiff ?? 0
    const diffColor = diff > 0 ? "text-red-500" : diff < 0 ? "text-blue-500" : "text-gray-500"
    const diffIcon = diff > 0 ? "▲" : diff < 0 ? "▼" : "-"

    return (
        <div>
            <div className="grid grid-cols-5 gap-1 mb-8">
                <div className="border border-red-500 p-5 bg-white">
                    <p className="text-sm text-gray-500 mb-2">오늘 체크인</p>
                    <p className="text-2xl font-bold text-red-500">{summaryData?.todayCheckInCount}</p>
                    <p className={`text-sm mb-2 ${diffColor}`}>
                        어제 대비 {diffIcon} {diff !== 0 ? Math.abs(diff) : ""}
                    </p>                </div>
                <div className="border border-gray-500 p-5 bg-white">
                    <p className="text-sm text-gray-500 mb-2">미배정 예약</p>
                    <p className="text-2xl font-bold text-gray-500">{summaryData?.unassignedCount}</p>
                </div>
                <div className="border border-gray-500 p-5 bg-white">
                    <p className="text-sm text-gray-500 mb-2">오늘 결제금액</p>
                    <p className="text-2xl font-bold text-gray-500">{summaryData?.todayPaymentAmount.toLocaleString()}</p>
                    <p className="text-sm text-gray-500 mb-2">결제 성공 {summaryData?.todayPaymentCount}건</p>
                </div>
                <div className="border border-gray-500 p-5 bg-white">
                    <p className="text-sm text-gray-500 mb-2">누적 미정산액</p>
                    <p className="text-2xl font-bold text-gray-500">{summaryData?.totalPendingBalance}</p>
                    <p className="text-sm text-gray-500 mb-2">호텔 {summaryData?.pendingHotelCount}곳</p>
                </div>
                <div className="border border-gray-500 p-5 bg-white">
                    <p className="text-sm text-gray-500 mb-2">오늘 결제 실패건</p>
                    <p className="text-2xl font-bold text-gray-500">{summaryData?.failedPaymentCount}</p>
                    <p className="text-sm text-gray-500 mb-2">최근 24시간</p>
                </div>
            </div>
        </div>
    )
}