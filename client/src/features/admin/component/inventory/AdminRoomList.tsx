import { Spinner } from "@/common/component/Spinner"
import { useRoomFilter } from "../../hooks/inventory/useRoomFilter"
import { useRoomList } from "../../hooks/inventory/useRoomList"
import { ErrorMessage } from "@/common/component/ErrorMessage"
import { Pagination } from "@/common/component/Pagination"
import { useRoomFilterOptions } from "../../hooks/inventory/useRoomFilterOption"
import { useEffect } from "react"
import { useQueryClient } from "@tanstack/react-query"

interface AdminRoomListProps {
    hotelId: number,
    inventorySelected: {roomTypeId: number|null, dateStr: string, totalInventory: number|null, totalReserved: number|null},
}

export const AdminRoomList = ({ hotelId, inventorySelected }: AdminRoomListProps) => {
    const { filter, setFloor, setRoomTypeId, setPage, setTargetDate } = useRoomFilter()
    const { optionData, isOptionError, isOptionLoading } = useRoomFilterOptions(hotelId)


    useEffect(()=>{
        if(inventorySelected.roomTypeId != null){
            setRoomTypeId(inventorySelected.roomTypeId)
            setTargetDate(inventorySelected.dateStr)
        }
    },[inventorySelected])
    const { roomListData, isRoomListError, isRoomListLoading } = useRoomList(hotelId, filter)

    const assginCount = roomListData?.content.filter(r=>r.assignable === false).length ?? 0

    if(isOptionLoading) return <Spinner/>
    if(isOptionError) return <ErrorMessage/>

    return (
        <div className="">
            <div className="flex items-center justify-between px-5 py-3 bg-gray-50 border-b border-gray-200">
                <span className="font-semibold text-sm">객실 목록</span>
            </div>
            <div className="flex justify-between mt-5 items-center gap-4 px-5 py-3 text-xs ">
                <p className="font-bold text-lg">{inventorySelected.dateStr ?? ""} 기준</p>
                <div className="flex gap-2">
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
            </div>
            <div className="flex mb-2 items-center">
                <span className="mx-3 w-3 h-3 rounded-lg bg-orange-500"></span>
                <p>배정 대기: <strong>{inventorySelected.totalReserved-assginCount}건</strong></p>
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
                                <th className="px-3 py-2 font-medium">층</th>
                                <th className="px-3 py-2 font-medium">호수</th>
                                <th className="px-3 py-2 font-medium">객실타입</th>
                                <th className="px-3 py-2 font-medium">점검상태</th>
                                <th className="px-3 py-2 font-medium">배정상태</th>
                            </tr>
                        </thead>
                        <tbody className="divide-y divide-gray-100 bg-white">
                            {roomListData?.content.map((r, i) => (
                                <tr key={r.roomId}
                                    className="hover:bg-gray-50 cursor-pointer"
                                >
                                    <td className="px-3 py-2 text-center">{i + 1}</td>
                                    <td className="px-3 py-2 text-center">{r.floor}</td>
                                    <td className="px-3 py-2 text-center">{r.roomNumber}</td>
                                    <td className="px-3 py-2 text-center">{r.roomTypeName}</td>
                                    <td className="px-3 py-2 text-center">
                                        {r.usable ? <span className="font-semibold text-gray-500">사용가능</span>
                                            : <span className="font-semibold text-red-500">점검중</span>}
                                    </td>
                                    <td className="px-3 py-2 text-center">
                                        {r.assignable ? <span className="font-semibold text-gray-500">미배정</span>
                                            : <span className="font-semibold text-red-500">배정완료</span>}
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