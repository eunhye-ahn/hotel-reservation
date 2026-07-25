import { useReservations } from "@/features/admin/hooks/useReservations"
import { useReservationsFilter } from "@/features/admin/hooks/useReservationsFilter"
import { Spinner } from "../../../component/common/Spinner"
import { ErrorMessage } from "../../../component/common/ErrorMessage"
import { useNavigate } from "react-router"


export const ReservationList = () => {
    const navigate = useNavigate()
    const { filter, setSearchType, setKeyword, setStartDate, setEndDate, setPage, setRoomAssigned, setStatus } = useReservationsFilter()
    const { data, isLoading, isError } = useReservations(filter)

    return (
        <div>
            <h1>예약 목록</h1>
            <div>
                <input type="date" value={filter.startDate ?? ""} onChange={(e) => setStartDate(e.target.value || undefined)} />
                <input type="date" value={filter.endDate ?? ""} onChange={(e) => setEndDate(e.target.value || undefined)} />
                <select value={filter.searchType ?? "USER_NAME"} onChange={(e) => {
                    setSearchType(e.target.value)
                    setKeyword("")
                }}>
                    <option value="USER_NAME">예약자명</option>
                    <option value="HOTEL_NAME">호텔명</option>
                    <option value="PHONE">전화번호</option>
                </select>
                <input type="text" value={filter.keyword ?? ""} onChange={(e) => setKeyword(e.target.value)} />
                <select value={filter.status ?? ""} onChange={(e) => {
                    setStatus(e.target.value || undefined)
                    setRoomAssigned(undefined)
                }}>
                    <option value="">전체</option>
                    <option value="PENDING_PAYMENT">결제미완료</option>
                    <option value="BEFORE_USE">이용전</option>
                    <option value="AFTER_USE">이용후</option>
                    <option value="CANCELED">취소</option>
                    <option value="EXPIRED">만료</option>
                </select>
                {filter.status === "BEFORE_USE" && <>
                    <label>
                        <input type="radio" name="roomAssigned"
                            checked={filter.roomAssigned === undefined}
                            onChange={() => setRoomAssigned(undefined)} />
                        전체
                    </label>
                    <label>
                        <input type="radio" name="roomAssigned"
                            checked={filter.roomAssigned === true}
                            onChange={() => setRoomAssigned(true)} />
                        배정
                    </label>
                    <label>
                        <input type="radio" name="roomAssigned"
                            checked={filter.roomAssigned === false}
                            onChange={() => setRoomAssigned(false)} />
                        미배정
                    </label>
                </>
                }
            </div>



            {isLoading ? (
                <Spinner />
            ) : isError ? (
                <ErrorMessage />
            ) : (
                <>
                    <table className="min-w-100 divide-y divide-gray-200">
                        <thead className="bg-gray-100">
                            <tr>
                                <th>예약번호</th>
                                <th>예약자</th>
                                <th>호텔명</th>
                                <th>객실타입</th>
                                <th>체크인</th>
                                <th>체크아웃</th>
                                <th>상태</th>
                                <th>배정여부</th>
                                <th>액션</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-200 bg-white">
                            {data?.content.map(r => (
                                <tr key={r.id}
                                    onClick={() => navigate(`/admin/reservations/${r.id}`)}
                                    className="hover:bg-gray-50">
                                    <td>{r.reservationKey}</td>
                                    <td>{r.username}</td>
                                    <td>{r.hotelName}</td>
                                    <td></td>
                                    <td>{r.startDate}</td>
                                    <td>{r.endDate}</td>
                                    <td>{r.reservationStatus}</td>
                                    <td>{r.roomAssigned ? '배정완료' : '미배정'}</td>
                                    <td onClick={(e) => e.stopPropagation()}>
                                        {r.reservationStatus === "BEFORE_USE" && (
                                            <>
                                                <button>
                                                    {r.roomAssigned ? "배정변경" : "배정"}
                                                </button>
                                                <button>
                                                    취소
                                                </button>
                                            </>
                                        )}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                    <div className="flex items-center justify-center gap-2">
                        <button
                            onClick={() => setPage(filter.page - 1)}
                            disabled={data?.first}
                        >이전</button>
                        <span>
                            {filter.page + 1}/{data?.totalPages}
                        </span>
                        <button
                            onClick={() => setPage(filter.page + 1)}
                            disabled={data?.last}
                        >다음
                        </button>
                    </div>
                </>
            )}
        </div>
    )
}