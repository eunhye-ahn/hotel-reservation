import { usePaymentFilter } from "@/features/admin/hooks/payment/usePaymentFilter"
import { usePaymentList } from "@/features/admin/hooks/payment/usePaymentList"
import { Spinner } from "@/common/component/Spinner"
import { ErrorMessage } from "@/common/component/ErrorMessage"
import { Pagination } from "@/common/component/Pagination"

export const PaymentList = () => {
    const { filter, setSearchType, setKeyword, setStartDate, setEndDate, setPage, setStatus } = usePaymentFilter()
    const { data, isLoading, isError } = usePaymentList(filter)

    return (
        <div className="">
            <h1 className="text-xl font-bold mb-4">결제목록</h1>

            <div className="flex flex-wrap items-center gap-2 mb-4">
                <input
                    type="date"
                    className="border border-gray-300 rounded-md px-2 py-1.5 text-sm"
                    value={filter.startDate ?? ""}
                    onChange={(e) => setStartDate(e.target.value || undefined)}
                />
                <input
                    type="date"
                    className="border border-gray-300 rounded-md px-2 py-1.5 text-sm"
                    value={filter.endDate ?? ""}
                    onChange={(e) => setEndDate(e.target.value || undefined)}
                />
                <select
                    className="border border-gray-300 rounded-md px-2 py-1.5 text-sm"
                    value={filter.searchType ?? "USER_NAME"}
                    onChange={(e) => setSearchType(e.target.value || undefined)}
                >
                    <option value="USER_NAME">결제자명</option>
                    <option value="HOTEL_NAME">호텔명</option>
                    <option value="PAYMENT_ID">결제ID</option>
                </select>
                <input
                    type="text"
                    className="border border-gray-300 rounded-md px-2 py-1.5 text-sm"
                    value={filter.keyword ?? ""}
                    onChange={(e) => setKeyword(e.target.value)}
                />
                <select
                    className="border border-gray-300 rounded-md px-2 py-1.5 text-sm"
                    value={filter.status ?? ""}
                    onChange={(e) => setStatus(e.target.value || undefined)}
                >
                    <option value="">전체</option>
                    <option value="NOT_STARTED">결제대기</option>
                    <option value="SUCCESS">결제완료</option>
                    <option value="FAILED">결제실패</option>
                    <option value="CANCELED">환불</option>
                </select>
            </div>

            {isLoading ? (
                <Spinner />
            ) : isError ? (
                <ErrorMessage />
            ) : (
                <>
                    <table className="w-full text-sm border border-gray-200 overflow-hidden">
                        <thead className="bg-gray-100">
                            <tr>
                                <th className="px-3 py-2 font-medium">결제ID</th>
                                <th className="px-3 py-2 font-medium">호텔명</th>
                                <th className="px-3 py-2 font-medium">결제자명</th>
                                <th className="px-3 py-2 font-medium">금액</th>
                                <th className="px-3 py-2 font-medium">결제상태</th>
                                <th className="px-3 py-2 font-medium">결제일시</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100 bg-white">
                            {data?.content?.map(payment =>
                                <tr key={payment.displayOrderNO} className="hover:bg-gray-50">
                                    <td className="px-3 py-2 text-center">{payment.displayOrderNO}</td>
                                    <td className="px-3 py-2">{payment.hotelName}</td>
                                    <td className="px-3 py-2 text-center">{payment.userName}</td>
                                    <td className="px-3 py-2 text-center">{payment.amount.toLocaleString()}원</td>
                                    <td className="px-3 py-2 text-center">{payment.status}</td>
                                    <td className="px-3 py-2 text-center">{payment.createdAt}</td>
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