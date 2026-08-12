import { Spinner } from "@/common/component/Spinner"
import { useRoomFilter } from "../../hooks/inventory/useRoomFilter"
import { useRoomList } from "../../hooks/inventory/useRoomList"
import { ErrorMessage } from "@/common/component/ErrorMessage"
import { Pagination } from "@/common/component/Pagination"
import { useRoomFilterOptions } from "../../hooks/inventory/useRoomFilterOption"


export const AdminRoomList = ({ hotelId }: { hotelId: number }) => {
    const { filter, setFloor, setRoomTypeId, setPage } = useRoomFilter()
    const { roomListData, isRoomListError, isRoomListLoading } = useRoomList(hotelId, filter)
    const { optionData, isOptionError, isOptionLoading } = useRoomFilterOptions(hotelId)

    if(isOptionLoading) return <Spinner/>
    if(isOptionError) return <ErrorMessage/>

    return (
        <div className="">
            <div className="flex items-center justify-between px-5 py-3 bg-gray-50 border-b border-gray-200">
                <span className="font-semibold text-sm">객실 목록</span>
            </div>
            <div className="flex justify-end items-center gap-4 px-5 py-3 text-xs ">
                <select
                    className="border border-gray-300 rounded-md px-2 py-1.5 text-sm"
                    value={filter.floor ?? ""}
                    onChange={(e) => setFloor(e.target.value ? Number(e.target.value) : undefined)}
                >
                    <option value="">전체층</option>
                    {
                        optionData?.floors.map(floor => (
                            <option key={floor} value={floor}>{floor}층</option>
                        ))
                    }
                </select>
                <select
                    className="border border-gray-300 rounded-md px-2 py-1.5 text-sm"
                    value={filter.roomTypeId ?? ""}
                    onChange={(e) => setRoomTypeId(e.target.value ? Number(e.target.value) : undefined)}
                >
                    <option value="">전체타입</option>
                    {optionData?.roomTypes.map(rt => (
                        <option key={rt.id} value={rt.id}>{rt.name}</option>
                    ))}
                </select>

            </div>

            {isRoomListLoading ? (
                <Spinner />
            ) : isRoomListError ? (
                <ErrorMessage />
            ) : (
                <>
                    <table className="w-full text-sm border border-gray-200 overflow-hidden">
                        <thead className="bg-gray-100">
                            <tr>
                                <th className="px-3 py-2 font-medium">연번</th>
                                <th className="px-3 py-2 font-medium">객실명</th>
                                <th className="px-3 py-2 font-medium">층</th>
                                <th className="px-3 py-2 font-medium">호수</th>
                                <th className="px-3 py-2 font-medium">객실타입</th>
                                <th className="px-3 py-2 font-medium">상태</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100 bg-white">
                            {roomListData?.content.map((r, i) => (
                                <tr key={r.roomId}
                                    className="hover:bg-gray-50 cursor-pointer"
                                >
                                    <td className="px-3 py-2 text-center">{i + 1}</td>
                                    <td className="px-3 py-2 text-center">{r.roomName}</td>
                                    <td className="px-3 py-2">{r.floor}</td>
                                    <td className="px-3 py-2 text-center">{r.roomNumber}</td>
                                    <td className="px-3 py-2 text-center">{r.roomTypeName}</td>
                                    <td className="px-3 py-2 text-center">
                                        {r.usable ? <span className="font-semibold text-gray-500">사용가능</span>
                                            : <span className="font-semibold text-red-500">점검중</span>}
                                    </td>
                                </tr>
                            ))}
                        </tbody>
                    </table>
                    <div className="mt-4">
                        <Pagination
                            page={filter.page}
                            totalPages={roomListData?.totalPages}
                            isFirst={roomListData?.first}
                            isLast={roomListData?.last}
                            onPageChange={setPage}
                        />
                    </div>
                </>
            )}
        </div>
    )
}