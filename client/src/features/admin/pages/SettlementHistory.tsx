
import { Spinner } from "@/common/component/Spinner"
import { useState } from "react"
import { useLocation, useParams } from "react-router"
import { SettlementModal } from "../component/settlement/SettlementModal"
import { Modal } from "@/common/component/Modal"
import { useSettlementHistoryFilter } from "../hooks/settlement/useSettlementHistoryFilter"
import { ErrorMessage } from "@/common/component/ErrorMessage"
import { useSettlementHistory } from "../hooks/settlement/useSettlementHistory"
import { Pagination } from "@/common/component/Pagination"
import { SettlementHistoryTable } from "../component/settlement/SettlementHistoryTable"

export const SettlementHistory = () => {
    const { hotelId } = useParams<{ hotelId: string }>()
    const location = useLocation()
    const state = location.state

    console.log(state)
    const [isOpen, setIsOpen] = useState<boolean>(false)
    const { filter, setEndDate, setStartDate, setStatus, setPage } = useSettlementHistoryFilter()
    const { data, isLoading, isError } = useSettlementHistory(Number(hotelId), filter)

    if (isLoading) return <Spinner />
    if (isError) return <ErrorMessage />

    return (
        <div>

            <div>
                <h2>호텔 정산 이력</h2>
                <p>{state.hotelName} _ 계좌: {state.sellerAccount}</p>
            </div>

            {/* 요약 */}
            <div className="grid grid-cols-4 gap-2 mb-8">
                <div className="border border-red-500 p-5 bg-white">
                    <p className="text-sm text-gray-500 mb-2">미정산 잔액</p>
                    <p className="text-2xl font-bold text-red-500">{state.pendingBalance}</p>
                </div>
                <div className="border border-gray-500 p-5 bg-white">
                    <p className="text-sm text-gray-500 mb-2">누적 정산액</p>
                    <p className="text-2xl font-bold text-gray-500">{state.totalSettlementAmount}</p>
                </div>
                <div className="border border-gray-500 p-5 bg-white">
                    <p className="text-sm text-gray-500 mb-2">정산 건수</p>
                    <p className="text-2xl font-bold text-gray-500">{data?.totalElements}</p>
                </div>
                <div className="border border-gray-500 p-5 bg-white">
                    <p className="text-sm text-gray-500 mb-2">최근 정산일</p>
                    <p className="text-2xl font-bold text-gray-500">{state.lastSettledAt ?? "없음"}</p>
                </div>
            </div>
            <div className="flex flex-wrap items-center gap-2 mb-4">
                <input
                    type="date"
                    value={filter.startDate ?? ""}
                    onChange={(e) => setStartDate(e.target.value ?? undefined)}
                    className="border border-gray-300 rounded-md px-2 py-1.5 text-sm" />
                <input
                    type="date"
                    value={filter.endDate ?? ""}
                    onChange={(e) => setEndDate(e.target.value ?? undefined)}
                    className="border border-gray-300 rounded-md px-2 py-1.5 text-sm" />
                <select value={filter.status ?? ""} onChange={(e) => setStatus(e.target.value ?? undefined)}>
                    <option value="">전체</option>
                    <option value="PENDING">완료</option>
                    <option value="COMPLETED">대기</option>
                    <option value="FAILED">실패</option>
                </select>
            </div>

            {/* 정산 이력 조회 테이블 */}
            <div>
                <div className="flex items-center justify-between px-5 py-3 bg-gray-50 border-b border-gray-200">
                    <span className="font-semibold text-sm">정산 이력</span>
                    <button
                        onClick={() => setIsOpen(true)}
                        className="px-4 py-2 text-sm bg-gray-900 text-white cursor-pointer hover:bg-gray-800">
                        수동 정산
                    </button>
                </div>
                <SettlementHistoryTable
                    settlements={data?.content ?? []}
                />
                <div className="mt-4">
                    <Pagination
                        page={filter.page}
                        totalPages={data?.totalPages}
                        isFirst={data?.first}
                        isLast={data?.last}
                        onPageChange={setPage}
                    />
                </div>

                <Modal
                    isOpen={isOpen}
                    onClose={() => setIsOpen(false)}
                    title="정산"
                >
                    <SettlementModal
                        hotelId={Number(hotelId)}
                        hotelName={state?.hotelName}
                        pendingBalance={state?.pendingBalance}
                        lastSettledAt={state?.lastSettledAt}
                        onSuccess={() => setIsOpen(false)}
                    />
                </Modal>
            </div>

        </div>
    )
}