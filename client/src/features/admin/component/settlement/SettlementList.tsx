import type { AdminSettlementSearchResponse } from "@/type/admin"
import { useState } from "react"
import { SettlementModal } from "./SettlementModal"
import { Modal } from "@/common/component/Modal"
import { useSettlementFilter } from "../../hooks/settlement/useSettlementFilter"
import { Spinner } from "@/common/component/Spinner"
import { ErrorMessage } from "@/common/component/ErrorMessage"
import { useSettlementList } from "../../hooks/settlement/useSettlementList"
import { Pagination } from "@/common/component/Pagination"

export const SettlementList = () => {
    const [settleTarget, setSettleTarget] = useState<AdminSettlementSearchResponse | null>(null)
    const { filter, setSearchType, setKeyword, setHasPendingBalance, setSortType, setPage } = useSettlementFilter()

    const { data, isLoading, isError } = useSettlementList(filter)

    return (
        <div>
            <h1>정산목록</h1>
            <select value={filter.searchType} onChange={(e) => setSearchType(e.target.value)}>
                <option value="HOTEL_NAME">호텔명</option>
                <option value="SELLER_ACCOUNT">계좌</option>
            </select>
            <input type="text" value={filter.keyword} onChange={(e) => setKeyword(e.target.value)} />
            <select value={filter.hasPendingBalance === undefined ? "" : String(filter.hasPendingBalance)}
                onChange={(e) => setHasPendingBalance(e.target.value === "" ? undefined : e.target.value === "true")}
            >
                <option value="">전체</option>
                <option value="true">미정산 있음</option>
                <option value="false">미정산 없음</option>
            </select>
            <select value={filter.sortType} onChange={(e) => setSortType(e.target.value)}>
                <option value="BALANCE">미정산액 많은 순</option>
                <option value="LAST_SETTLED_DESC">최근 정산 순</option>
                <option value="TOTAL_AMOUNT">누적정산액 많은 순</option>
            </select>
            {isLoading ? <Spinner />
                : isError ? <ErrorMessage /> : (
                    <>
                        <table>
                            <thead>
                                <tr>
                                    <th>연번</th>
                                    <th>호텔명</th>
                                    <th>정산계좌</th>
                                    <th>미정산 잔액</th>
                                    <th>누적 정산액</th>
                                    <th>최근 정산일</th>
                                    <th>액션</th>
                                </tr>
                            </thead>
                            <tbody>
                                {data?.content.map((s, i) =>
                                    <tr key={s.hotelId}>
                                        <td>{i + 1}</td>
                                        <td>{s.hotelName}</td>
                                        <td>{s.sellerAccount}</td>
                                        <td>{s.pendingBalance}</td>
                                        <td>{s.totalSettlementAmount}</td>
                                        <td>{s.lastSettledAt ?? "없음"}</td>
                                        <td>
                                            <button onClick={() => setSettleTarget(s)}>정산하기</button>
                                            <button>정산이력</button>
                                        </td>
                                    </tr>
                                )}
                            </tbody>
                        </table>
                        <Pagination
                            page={filter.page}
                            totalPages={data?.totalPages}
                            isFirst={data?.first}
                            isLast={data?.last}
                            onPageChange={setPage}
                        />
                    </>
                )}



            <Modal
                isOpen={settleTarget != null}
                onClose={() => setSettleTarget(null)}
                title="정산"
            >
                {settleTarget && (
                    <SettlementModal
                        hotelId={settleTarget.hotelId}
                        hotelName={settleTarget.hotelName}
                        pendingBalance={settleTarget.pendingBalance}
                        lastSettledAt={settleTarget.lastSettledAt}
                        onClose={() => setSettleTarget(null)}
                    />
                )}
            </Modal>

        </div>
    )
}