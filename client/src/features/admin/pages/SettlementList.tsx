import type { AdminSettlementSearchResponse } from "@/type/admin"
import { useState } from "react"
import { Spinner } from "@/common/component/Spinner"
import { ErrorMessage } from "@/common/component/ErrorMessage"
import { Pagination } from "@/common/component/Pagination"
import { useNavigate } from "react-router"
import { useSettlementList } from "../hooks/settlement/useSettlementList"
import { useSettlementFilter } from "../hooks/settlement/useSettlementFilter"

export const SettlementList = () => {
    const [settleTarget, setSettleTarget] = useState<AdminSettlementSearchResponse | null>(null)
    const { filter, setSearchType, setKeyword, setHasPendingBalance, setSortType, setPage } = useSettlementFilter()

    const { data, isLoading, isError } = useSettlementList(filter)
    const navigate = useNavigate()

    return (
        <div>
            <h1 className="text-xl font-bold mb-4">정산목록</h1>

            <div className="flex flex-wrap items-center gap-2 mb-4">
                <select
                    className="border border-gray-300 rounded-md px-2 py-1.5 text-sm"
                    value={filter.searchType}
                    onChange={(e) => setSearchType(e.target.value)}
                >
                    <option value="HOTEL_NAME">호텔명</option>
                    <option value="SELLER_ACCOUNT">계좌</option>
                </select>
                <input
                    type="text"
                    className="border border-gray-300 rounded-md px-2 py-1.5 text-sm"
                    value={filter.keyword}
                    onChange={(e) => setKeyword(e.target.value)}
                />
                <select
                    className="border border-gray-300 rounded-md px-2 py-1.5 text-sm"
                    value={filter.hasPendingBalance === undefined ? "" : String(filter.hasPendingBalance)}
                    onChange={(e) => setHasPendingBalance(e.target.value === "" ? undefined : e.target.value === "true")}
                >
                    <option value="">전체</option>
                    <option value="true">미정산 있음</option>
                    <option value="false">미정산 없음</option>
                </select>
                <select
                    className="border border-gray-300 rounded-md px-2 py-1.5 text-sm"
                    value={filter.sortType}
                    onChange={(e) => setSortType(e.target.value)}
                >
                    <option value="BALANCE">미정산액 많은 순</option>
                    <option value="LAST_SETTLED_DESC">최근 정산 순</option>
                    <option value="TOTAL_AMOUNT">누적정산액 많은 순</option>
                </select>
            </div>

            {isLoading ? <Spinner />
                : isError ? <ErrorMessage /> : (
                    <>
                        <table className="w-full text-sm border border-gray-200 overflow-hidden">
                            <thead className="bg-gray-100">
                                <tr>
                                    <th className="px-3 py-2 font-medium">연번</th>
                                    <th className="px-3 py-2 font-medium">호텔명</th>
                                    <th className="px-3 py-2 font-medium">정산계좌</th>
                                    <th className="px-3 py-2 font-medium">미정산 잔액</th>
                                    <th className="px-3 py-2 font-medium">누적 정산액</th>
                                    <th className="px-3 py-2 font-medium">최근 정산일</th>
                                </tr>
                            </thead>
                            <tbody className="divide-y divide-gray-100 bg-white">
                                {data?.content.map((s, i) =>
                                    <tr key={s.hotelId} className="hover:bg-gray-50"
                                        onClick={() => navigate(`/admin/settlements/${s.hotelId}`, {
                                            state: {
                                                hotelName: s.hotelName,
                                                sellerAccount: s.sellerAccount,
                                                pendingBalance: s.pendingBalance,
                                                totalSettlementAmount: s.totalSettlementAmount,
                                                lastSettledAt: s.lastSettledAt
                                            }
                                        })}>
                                        <td className="px-3 py-2 text-center">{i + 1}</td>
                                        <td className="px-3 py-2">{s.hotelName}</td>
                                        <td className="px-3 py-2 text-center">{s.sellerAccount}</td>
                                        <td className="px-3 py-2 text-center">{s.pendingBalance.toLocaleString()}원</td>
                                        <td className="px-3 py-2 text-center">{s.totalSettlementAmount.toLocaleString()}원</td>
                                        <td className="px-3 py-2 text-center">{s.lastSettledAt ?? "없음"}</td>

                                    </tr>
                                )}
                            </tbody>
                        </table>

                        <div className="mt-4">
                            <Pagination
                                page={filter.page}
                                totalPages={data?.totalPages}
                                isFirst={data?.first}
                                isLast={data?.last}
                                onPageChange={setPage}
                            />
                        </div>
                    </>
                )}
        </div>
    )
}