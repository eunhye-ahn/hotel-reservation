import { usePaymentFilter } from "@/features/admin/hooks/payment/usePaymentFilter"
import { usePaymentList } from "@/features/admin/hooks/payment/usePaymentList"
import { Spinner } from "@/common/component/Spinner"
import { ErrorMessage } from "@/common/component/ErrorMessage"
import { Pagination } from "@/common/component/Pagination"

export const PaymentList = () => {
    const { filter, setSearchType, setKeyword, setStartDate, setEndDate, setPage, setStatus } = usePaymentFilter()
    const { data, isLoading, isError } = usePaymentList(filter)

    return (
        <div>
            <h1>결제목록</h1>
            <input type="date" value={filter.startDate ?? ""} onChange={(e) => setStartDate(e.target.value || undefined)} />
            <input type="date" value={filter.endDate ?? ""} onChange={(e) => setEndDate(e.target.value || undefined)} />
            <select value={filter.searchType ?? "USER_NAME"} onChange={(e) => setSearchType(e.target.value || undefined)}>
                <option value="USER_NAME">결제자명</option>
                <option value="HOTEL_NAME">호텔명</option>
                <option value="PAYMENT_ID">결제ID</option>
            </select>
            <input type="text" value={filter.keyword ?? ""} onChange={(e) => setKeyword(e.target.value)} />
            <select value={filter.status ?? ""} onChange={(e) => setStatus(e.target.value || undefined)}>
                <option value="">전체</option>
                <option value="NOT_STARTED">결제대기</option>
                <option value="SUCCESS">결제완료</option>
                <option value="FAILED">결제실패</option>
                <option value="CANCELED">환불</option>
            </select>
            {isLoading ? (
                <Spinner />
            ) : isError ? (
                <ErrorMessage />
            ) : (
                <>
                    <table>
                        <thead>
                            <tr>
                                <th>결제ID</th>
                                <th>호텔명</th>
                                <th>결제자명</th>
                                <th>금액</th>
                                <th>결제상태</th>
                                <th>결제일시</th>
                            </tr>
                        </thead>
                        <tbody>
                            {data?.content?.map(payment =>
                                <tr>
                                    <td>{payment.displayOrderNO}</td>
                                    <td>{payment.hotelName}</td>
                                    <td>{payment.userName}</td>
                                    <td>{payment.amount}</td>
                                    <td>{payment.status}</td>
                                    <td>{payment.createdAt}</td>
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
            )
            }
        </div>
    )
}