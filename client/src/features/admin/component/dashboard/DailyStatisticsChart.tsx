import { BarChart, Bar, XAxis, YAxis, Tooltip, Legend, ResponsiveContainer } from "recharts"
import { useDailyStatistics } from "../../hooks/dashboard/useDailyStatistics"

export const DailyStatisticsChart = () => {
    const { statisticsData, isStatisticsError, isStatisticsLoading } = useDailyStatistics()

    const chartData = statisticsData?.map(d => ({
        date: d.date,
        예약건수: d.reservationCount,
        결제금액: Math.round(d.paymentTotal / 10000)
    }))

    return (
        <div>
            <div className="flex items-center justify-between px-5 py-3 bg-gray-50 border-b border-gray-200">
                <span className="font-semibold text-sm">객실 목록</span>
            </div>
            <ResponsiveContainer width="100%" height={300}>
                <BarChart width={600} height={300} data={chartData}>
                    <XAxis dataKey="date" />
                    <YAxis yAxisId="left" tick={{ fontSize: 12 }} />
                    <YAxis yAxisId="right" orientation="right" tick={{ fontSize: 12 }} />
                    <Tooltip />
                    <Legend />
                    <Bar yAxisId="left" dataKey="예약건수" fill="#2563eb" />
                    <Bar yAxisId="right" dataKey="결제금액" fill="#16a34a" />
                </BarChart>
            </ResponsiveContainer>
        </div>
    )
}