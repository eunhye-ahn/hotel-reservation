import { getReservations } from "@/api/api"
import { useQuery } from "@tanstack/react-query"
import { useState } from "react";

export const RoomAssignment = () => {
    const [searchType, setSearchType] = useState<string | undefined>(undefined);
    const [keyword, setKeyword] = useState("");
    const [startDate, setStartDate] = useState<string | undefined>(undefined);
    const [endDate, setEndDate] = useState<string | undefined>(undefined);
    const [status, setStatus] = useState<string | undefined>(undefined);
    const [page, setPage] = useState(0);

    const { data } = useQuery({
        queryKey: ["reservations"],
        queryFn: () => getReservations(searchType, keyword, startDate, endDate, status, page).then((res) => res.data)
    })

    console.log(data);

    return (
        <div>
            <h1>배정 및 예약확정</h1>
            <table className="min-w-100 drive-y drive-gray-200">
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
                        <tr key={r.id} className="hover:bg-gray-50">
                            <td>{r.reservationKey}</td>
                            <td>{r.username}</td>
                            <td>{r.hotelName}</td>
                            <td></td>
                            <td>{r.startDate}</td>
                            <td>{r.endDate}</td>
                            <td>{r.reservationStatus}</td>
                            <td>{r.roomAssigned == true ? '완료' : '미정'}</td>
                            <td></td>
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    )
}