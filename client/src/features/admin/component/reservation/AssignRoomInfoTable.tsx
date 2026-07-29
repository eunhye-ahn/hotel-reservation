interface AssignRoomInfoTableProps {
    roomNumber: number,
    roomName: string,
    floor: number,
    usable: boolean
}

export const AssignRoomInfoTable = ({ roomNumber, roomName, floor, usable }: AssignRoomInfoTableProps) => {
    return (
        <div className="p-3">
            <p className="font-semibold text-sm">배정 정보</p>
            <table className="mb-3">
                <tbody>
                    <tr>
                        <th className="text-gray-500 text-left w-20">호수</th>
                        <td className="font-bold">{roomNumber}</td>
                    </tr>
                    <tr>
                        <th className="text-gray-500 text-left">객실명</th>
                        <td>{roomName}</td>
                    </tr>
                    <tr>
                        <th className="text-gray-500 text-left">층</th>
                        <td>{floor}</td>
                    </tr>
                    <tr>
                        <th className="text-gray-500 text-left">객실상태</th>
                        <td>{usable ? '사용가능' : '사용불가'}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    )
}