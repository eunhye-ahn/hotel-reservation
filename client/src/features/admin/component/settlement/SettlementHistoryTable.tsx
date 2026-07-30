import type { Page, SettlementHistoryResponse } from "@/type/hotel"
import { SettlementStatusBadge } from "./SettlementStatusBadge"

export const SettlementHistoryTable = ({ settlements }: { settlements: SettlementHistoryResponse[] }) => {
    return (
        <table className="w-full text-sm">

            <thead className="bg-gray-50 border-y border-gray-200">

                <tr>
                    <th className="px-3 py-2 font-medium text-left">연번</th>
                    <th className="px-3 py-2 font-medium text-left">정산ID</th>
                    <th className="px-3 py-2 font-medium text-left">정산기간</th>
                    <th className="px-3 py-2 font-medium text-right">금액</th>
                    <th className="px-3 py-2 font-medium text-left">상태</th>
                    <th className="px-3 py-2 font-medium text-left">생성일시</th>
                    <th className="px-3 py-2 font-medium text-left">정산완료일시</th>
                </tr>
            </thead>
            <tbody className="divide-y divide-gray-100">
                {settlements.map((s, i) => (
                    <tr key={s.settlementId}>
                        <td className="px-3 py-2.5">{i + 1}</td>
                        <td className="px-3 py-2.5">{s.settlementId}</td>
                        <td className="px-3 py-2.5">{s.periodStartDate} ~ {s.periodEndDate}</td>
                        <td className="px-3 py-2.5 text-right font-semibold">{s.amount.toLocaleString()}원</td>
                        <td className="px-3 py-2.5">
                            <SettlementStatusBadge
                                status={s.status}
                            />
                        </td>
                        <td className="px-3 py-2.5 text-gray-500">{s.createdAt}</td>
                        <td className="px-3 py-2.5 text-gray-500">{s.settledAt ?? "-"}</td>
                    </tr>
                ))}
            </tbody>
        </table>
    )
}