import { Spinner } from "@/common/component/Spinner"
import { useInventorySummary } from "../hooks/inventory/useInventorySummary"
import { useInventorySummaryFilter } from "../hooks/inventory/useInventorySummaryFilter"
import { ErrorMessage } from "@/common/component/ErrorMessage"
import { Pagination } from "@/common/component/Pagination"
import { useNavigate } from "react-router"

export const InventorySummaryList = () => {
    const { filter, setDate, setHotelName, setSortType, setPage } = useInventorySummaryFilter()
    const { data, isLoading, isError } = useInventorySummary(filter)

    const navigate = useNavigate()

    console.log(data)

    return (
        <div>
            <div className="flex flex-wrap items-center gap-2 mb-4">
                <input
                    type="date"
                    value={filter.date}
                    onChange={(e) => setDate(e.target.value ?? undefined)}
                    className="border border-gray-300 rounded-md px-2 py-1.5 text-sm" />
                <input
                    type="text"
                    className="border border-gray-300 rounded-md px-2 py-1.5 text-sm"
                    placeholder="호텔명 검색"
                    value={filter.hotelName ?? ""}
                    onChange={(e) => setHotelName(e.target.value)}
                />
                <select value={filter.sortType} onChange={(e) => setSortType(e.target.value)}>
                    <option value="RESERVE_RATE_DESC">예약율 높은순</option>
                    <option value="RESERVE_RATE_ASC">예약율 낮은순</option>
                    <option value="AVAILABLE_ASC">잔여객실 적은 순</option>
                </select>
            </div>
            {isLoading ? <Spinner />
                : isError ? <ErrorMessage />
                    : <table className="w-full text-sm">

                        <thead className="bg-gray-50 border-y border-gray-200">

                            <tr>
                                <th className="px-3 py-2 font-medium text-left">연번</th>
                                <th className="px-3 py-2 font-medium text-left">호텔명</th>
                                <th className="px-3 py-2 font-medium text-left">지역</th>
                                <th className="px-3 py-2 font-medium text-right">객실타입수</th>
                                <th className="px-3 py-2 font-medium text-left">총재고</th>
                                <th className="px-3 py-2 font-medium text-left">예약수</th>
                                <th className="px-3 py-2 font-medium text-left">잔여객실수</th>
                                <th className="px-3 py-2 font-medium text-left">예약율</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100">
                            {data?.content.map((h, i) => (
                                <tr key={h.hotelId} onClick={() => navigate(`/admin/inventory/${h.hotelId}`)}>
                                    <td className="px-3 py-2.5">{i + 1}</td>
                                    <td className="px-3 py-2.5">{h.hotelName}</td>
                                    <td className="px-3 py-2.5">{h.ldongSignguCd}</td>
                                    <td className="px-3 py-2.5 text-right font-semibold">{h.roomTypeCount}</td>
                                    <td className="px-3 py-2.5">
                                        {h.totalInventory}
                                    </td>
                                    <td className="px-3 py-2.5 text-gray-500">{h.totalReserved}</td>
                                    <td className="px-3 py-2.5 text-gray-500">{h.availableCount}</td>
                                    <td className="px-3 py-2.5 text-gray-500">{h.reserveRate}</td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
            }
            <Pagination
                page={filter.page}
                totalPages={data?.totalPages}
                isFirst={data?.first}
                isLast={data?.last}
                onPageChange={setPage}
            />
        </div>
    )
}