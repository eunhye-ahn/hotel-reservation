interface AssignRoomInfoTableProps {
    roomNumber: number,
    roomName: string,
    floor: number,
    usable: boolean
}

export const AssignRoomInfoTable = ({ roomNumber, roomName, floor, usable }: AssignRoomInfoTableProps) => {
    return (
        <div>
            <p>배정 정보</p>
            <table>
                <tbody>
                    <tr>
                        <th>호수</th>
                        <td>{roomNumber}</td>
                    </tr>
                    <tr>
                        <th>객실명</th>
                        <td>{roomName}</td>
                    </tr>
                    <tr>
                        <th>층</th>
                        <td>{floor}</td>
                    </tr>
                    <tr>
                        <th>객실상태</th>
                        <td>{usable ? '사용가능' : '사용불가'}</td>
                    </tr>
                </tbody>
            </table>
        </div>
    )
}