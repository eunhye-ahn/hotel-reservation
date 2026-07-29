import { ErrorMessage } from "@/common/component/ErrorMessage"
import { Spinner } from "@/common/component/Spinner"
import { useState } from "react"
import { useRoomsByReservation } from "@/features/admin/hooks/reservation/useRoomsByReservation"
import { useAssignRoom } from "../../hooks/reservation/useAssignRoom"
import { useUnassignRoom } from "../../hooks/reservation/useUnAssignRoom"

interface RoomAssignmentFormProps {
    reservationId: number
}

export const RoomAssignmentForm = ({ reservationId }: RoomAssignmentFormProps) => {
    const [showOnlyAvailable, setShowOnlyAvailable] = useState<boolean>(false)

    const { data, isLoading, isError } = useRoomsByReservation(reservationId)
    const { assignRoomMutate, isAssigning } = useAssignRoom(reservationId)
    const { unassignRoomMutate, isUnAssigning } = useUnassignRoom(reservationId)

    if (isLoading) return <Spinner />
    if (isError) return <ErrorMessage />

    const rooms = showOnlyAvailable ? data?.filter(room => room.available) : data

    return (
        <div>
            <label className="flex items-center gap-2 text-sm mb-3 cursor-pointer">
                <input type="checkbox" checked={showOnlyAvailable}
                    onChange={(e) => setShowOnlyAvailable(e.target.checked)}
                />
                배정가능만 보기
            </label>

            <table className="w-full text-sm border border-gray-200 overflow-hidden">
                <thead className="bg-gray-100">
                    <tr>
                        <th className="px-3 py-2 text-center font-medium whitespace-nowrap">연번</th>
                        <th className="px-3 py-2 text-center font-medium whitespace-nowrap">방이름</th>
                        <th className="px-3 py-2 text-center font-medium whitespace-nowrap">층</th>
                        <th className="px-3 py-2 text-center font-medium whitespace-nowrap">호수</th>
                        <th className="px-3 py-2 text-center font-medium whitespace-nowrap">방타입</th>
                        <th className="px-3 py-2 text-center font-medium whitespace-nowrap">상태</th>
                        <th className="px-3 py-2 text-center font-medium whitespace-nowrap">액션</th>
                    </tr>
                </thead>
                <tbody className="divide-y divide-gray-100 bg-white">
                    {rooms?.map((room, i) =>
                        <tr key={room.id} className="hover:bg-gray-50">
                            <td className="px-3 py-2 text-center">{i + 1}</td>
                            <td className="px-3 py-2 text-center">{room.roomName}</td>
                            <td className="px-3 py-2 text-center">{room.floor}층</td>
                            <td className="px-3 py-2 text-center">{room.roomNumber}호</td>
                            <td className="px-3 py-2 text-center">{room.roomTypeName}</td>
                            <td className="px-3 py-2 text-center">
                                {room.currentlyAssigned ? '(현재배정) ' : ''}
                                {!room.roomStatus ? '점검중' : room.available ? '배정가능' : '배정불가'}
                            </td>
                            <td className="px-3 py-2 text-center">
                                {room.currentlyAssigned ? (
                                    <button
                                        className="text-xs px-2.5 py-1 border border-red-300 text-red-500 cursor-pointer hover:bg-red-50 disabled:opacity-40"
                                        onClick={() => unassignRoomMutate()}
                                        disabled={isUnAssigning}
                                    >
                                        배정취소
                                    </button>
                                ) : (
                                    <button
                                        className="text-xs px-2.5 py-1 border border-gray-300 cursor-pointer hover:bg-gray-100 disabled:opacity-40 disabled:cursor-not-allowed"
                                        disabled={!room.roomStatus || !room.available || isAssigning}
                                        onClick={() => assignRoomMutate(room.id)}
                                    >
                                        배정하기
                                    </button>
                                )}
                            </td>
                        </tr>
                    )}
                    {rooms?.length === 0 && (
                        <tr>
                            <td colSpan={7} className="px-3 py-6 text-center text-gray-400">
                                배정가능한 방이 없습니다
                            </td>
                        </tr>
                    )}
                </tbody>
            </table>
        </div>
    )
}