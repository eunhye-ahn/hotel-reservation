import { ErrorMessage } from "@/component/common/ErrorMessage"
import { Spinner } from "@/component/common/Spinner"
import { useState } from "react"
import { useRoomsByReservation } from "@/features/admin/hooks/reservation/useRoomsByReservation"
import { useAssignRoom } from "../hooks/reservation/useAssignRoom"
import { useUnassignRoom } from "../hooks/reservation/useUnAssignRoom"

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
            <div>
                <label>
                    <input type="checkbox" checked={showOnlyAvailable}
                        onChange={(e) => setShowOnlyAvailable(e.target.checked)}
                    />
                    배정가능
                </label>
            </div>
            <table>
                <thead>
                    <tr>
                        <th>연번</th>
                        <th>방이름</th>
                        <th>층</th>
                        <th>호수</th>
                        <th>방타입</th>
                        <th>상태</th>
                        <th>액션</th>
                    </tr>
                </thead>
                <tbody>
                    {rooms?.map((room, i) =>
                        <tr key={room.id}>
                            <td>{i + 1}</td>
                            <td>{room.roomName}</td>
                            <td>{room.floor}층</td>
                            <td>{room.roomNumber}호</td>
                            <td>{room.roomTypeName}</td>
                            <td>
                                {room.currentlyAssigned ? '(현재배정)' : ''}
                                {!room.roomStatus ? '점검중' : room.available ? '배정가능' : '배정불가'}
                            </td>
                            <td>
                                {room.currentlyAssigned ? (
                                    <button onClick={() => unassignRoomMutate()} disabled={isUnAssigning}>
                                        배정취소
                                    </button>
                                ) : (
                                    <button
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
                            <td colSpan={7}>
                                배정가능한 방이 없습니다
                            </td>
                        </tr>
                    )}
                </tbody>
            </table>
        </div>
    )
}