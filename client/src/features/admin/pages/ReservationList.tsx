import { useReservations } from "@/features/admin/hooks/reservation/useReservations"
import { useReservationsFilter } from "@/features/admin/hooks/reservation/useReservationsFilter"
import { Spinner } from "@/common/component/Spinner"
import { ErrorMessage } from "@/common/component/ErrorMessage"
import { useNavigate } from "react-router"
import { Pagination } from "@/common/component/Pagination"
import { ReservationStatusBadge } from "../component/reservation/ReservationStatusBadge"

export const ReservationList = () => {
    const navigate = useNavigate()
    const { filter, setSearchType, setKeyword, setStartDate, setEndDate, setPage, setRoomAssigned, setStatus } = useReservationsFilter()
    const { data, isLoading, isError } = useReservations(filter)

    return (
        <div className="">
            <h1 className="text-xl font-bold mb-4">예약 목록</h1>

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
                    onChange={(e) => {
                        setSearchType(e.target.value)
                        setKeyword("")
                    }}
                >
                    <option value="USER_NAME">예약자명</option>
                    <option value="HOTEL_NAME">호텔명</option>
                    <option value="PHONE">전화번호</option>
                    <option value="RESERVE_ID">예약ID</option>
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
                    onChange={(e) => {
                        setStatus(e.target.value || undefined)
                        setRoomAssigned(undefined)
                    }}
                >
                    <option value="">전체</option>
                    <option value="BEFORE_USE">이용전</option>
                    <option value="AFTER_USE">이용후</option>
                    <option value="CANCELED">취소</option>
                    <option value="CANCELED_PENDING">취소대기</option>
                    <option value="EXPIRED">만료</option>
                </select>
                {filter.status === "BEFORE_USE" && (
                    <div className="flex items-center gap-3 text-sm">
                        <label className="flex items-center gap-1">
                            <input type="radio" name="roomAssigned"
                                checked={filter.roomAssigned === undefined}
                                onChange={() => setRoomAssigned(undefined)} />
                            전체
                        </label>
                        <label className="flex items-center gap-1">
                            <input type="radio" name="roomAssigned"
                                checked={filter.roomAssigned === true}
                                onChange={() => setRoomAssigned(true)} />
                            배정
                        </label>
                        <label className="flex items-center gap-1">
                            <input type="radio" name="roomAssigned"
                                checked={filter.roomAssigned === false}
                                onChange={() => setRoomAssigned(false)} />
                            미배정
                        </label>
                    </div>
                )}
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
                                <th className="px-3 py-2 font-medium">연번</th>
                                <th className="px-3 py-2 font-medium">예약ID</th>
                                <th className="px-3 py-2 font-medium">예약자</th>
                                <th className="px-3 py-2 font-medium">호텔명</th>
                                <th className="px-3 py-2 font-medium">객실타입</th>
                                <th className="px-3 py-2 font-medium">체크인</th>
                                <th className="px-3 py-2 font-medium">체크아웃</th>
                                <th className="px-3 py-2 font-medium">상태</th>
                                <th className="px-3 py-2 font-medium">배정여부</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100 bg-white">
                            {data?.content.map((r,idx) => (
                                <tr key={r.id}
                                    onClick={() => navigate(`/admin/reservations/${r.id}`)}
                                    className="hover:bg-gray-50 cursor-pointer"
                                >
                                    <td className="px-3 py-2 text-center">{idx+1}</td>
                                    <td className="px-3 py-2 text-center">{r.displayReservationNO}</td>
                                    <td className="px-3 py-2 text-center">{r.username}</td>
                                    <td className="px-3 py-2">{r.hotelName}</td>
                                    <td className="px-3 py-2 text-center">{r.roomTypeName}</td>
                                    <td className="px-3 py-2 text-center">{r.startDate}</td>
                                    <td className="px-3 py-2 text-center">{r.endDate}</td>
                                    <td className="px-3 py-2 text-center">
                                        <ReservationStatusBadge
                                            status={r.reservationStatus}
                                        />
                                    </td>
                                    <td className="px-3 py-2 text-center">{r.roomAssigned ? '배정완료' : '미배정'}</td>
                                </tr>
                            ))}
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