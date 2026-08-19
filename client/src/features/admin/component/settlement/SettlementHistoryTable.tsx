import type { SettlementHistoryResponse } from "@/api/types/hotel"
import { SettlementStatusBadge } from "./SettlementStatusBadge"
import { format } from "date-fns"

export const SettlementHistoryTable = ({ settlements }: { settlements: SettlementHistoryResponse[] }) => {
    return (
        <table className="w-full text-sm">

            <thead className="bg-gray-50 border-y border-gray-200">

                <tr>
                    <th className="px-3 py-2 font-medium">연번</th>
                    <th className="px-3 py-2 font-medium">정산ID</th>
                    <th className="px-3 py-2 font-medium">정산기간</th>
                    <th className="px-3 py-2 font-medium">금액</th>
                    <th className="px-3 py-2 font-medium">상태</th>
                    <th className="px-3 py-2 font-medium">생성일시</th>
                    <th className="px-3 py-2 font-medium">정산완료일시</th>
                </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
                {settlements.map((s, i) => (
                    <tr key={s.settlementId}>
                        <td className="px-3 py-2.5 text-center">{i + 1}</td>
                        <td className="px-3 py-2.5 text-center">{s.settlementId}</td>
                        <td className="px-3 py-2.5 text-center">{s.periodStartDate} ~ {s.periodEndDate}</td>
                        <td className="px-3 py-2.5 text-center font-semibold">{s.amount.toLocaleString()}원</td>
                        <td className="px-3 py-2.5 text-center">
                            <SettlementStatusBadge
                                status={s.status}
                            />
                        </td>
                        <td className="px-3 py-2.5 text-gray-500 text-center">
                            {s.settledAt ? format(new Date(s.settledAt),"yyyy-MM-dd HH:mm") : "-"}
                        </td>
                        <td className="px-3 py-2.5 text-gray-500  text-center">{s.settledAt ? format(new Date(s.settledAt),"yyyy-MM-dd HH:mm") : "-"}</td>
                    </tr>
                ))}
            </tbody>
        </table>
    )
}