import { Spinner } from "@/common/component/Spinner"
import { useReserveMothStatistics } from "../../hooks/dashboard/useReserveMonthStatistics"
import { ErrorMessage } from "@/common/component/ErrorMessage"
import { Cell, Pie, PieChart, ResponsiveContainer, Tooltip } from "recharts"

const STATUS_COLOR: Record<string, string> = {
    BEFORE_USE: "#2563eb",
    EXPIRED: "#9ca3af",
    CANCELED: "#dc2626",
    AFTER_USE: "yellow"
}

const STATUS_LABEL: Record<string, string> = {
    BEFORE_USE: "이용 전",
    EXPIRED: "만료",
    CANCELED: "취소",
    AFTER_USE: "이용 후"
}

export const ReserveStatisticsChart = () => {
    const { reserveData, isReserveError, isReserveLoading } = useReserveMothStatistics()

    if (isReserveLoading) return <Spinner />
    if (isReserveError) return <ErrorMessage />

    const total = reserveData?.reduce((sum, d) => sum + d.count, 0) ?? 0

    return (
        <div className="">
            <div className="flex items-center justify-between px-5 py-3 bg-gray-50 border-b border-gray-200">
                <span className="font-semibold text-sm">예약 상태 분포</span>
            </div>
            <div className="flex items-center">
                <div className="relative w-[180px] h-[180px]">
                    <ResponsiveContainer width="100%" height="100%">
                        <PieChart>
                            <Pie
                                data={reserveData}
                                dataKey="count"
                                nameKey="status"
                                innerRadius={50}
                                outerRadius={80}
                                paddingAngle={2}
                            >
                                {reserveData?.map((r) => (
                                    <Cell key={r.status} fill={STATUS_COLOR[r.status] ?? "#ccc"} />
                                ))}
                            </Pie>
                            <Tooltip />
                        </PieChart>
                    </ResponsiveContainer>

                    <div className="absolute inset-0 flex items-center justify-center">
                        <p className="text-lg font-bold">{total}건</p>
                    </div>


                </div>
                <div className="flex flex-col  gap-2 ml-6">
                    {reserveData?.map((r) => (
                        <div key={r.status} className="flex items-center gap-2 text-sm">
                            <span
                                className="w-3 h-3"
                                style={{ backgroundColor: STATUS_COLOR[r.status] ?? "#ccc" }}
                            />
                            <span>{STATUS_LABEL[r.status] ?? r.status}</span>
                            <span className="font-semibold">{r.count}</span>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    )
}