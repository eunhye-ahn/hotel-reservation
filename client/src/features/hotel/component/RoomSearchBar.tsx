
interface RoomSearchBarProps {
    startDate: string,
    endDate: string,
    numberOfGuests: number,
    numberOfRooms: number,
    onStartDateChange: (value: string) => void,
    onEndDateChange: (value: string) => void,
    onRoomsChange: (value: number) => void,
    onGuestsChange: (value: number) => void,
}

export const RoomSearchBar = ({
    startDate,
    endDate,
    numberOfGuests,
    numberOfRooms,
    onStartDateChange,
    onEndDateChange,
    onRoomsChange,
    onGuestsChange
}: RoomSearchBarProps) => {
    return (
        <div className="flex items-center gap-3 p-4 border border-gray-200 rounded-xl my-6">
            <input
                type="date"
                className="border-none outline-none text-sm"
                value={startDate}
                onChange={(e) => onStartDateChange(e.target.value)}
            />
            <span className="text-gray-400"> ~ </span>
            <input
                type="date"
                className="border-none outline-none text-sm"
                value={endDate}
                onChange={(e) => onEndDateChange(e.target.value)}
            />
            <div className="flex items-center gap-2 ml-auto">
                <span className="text-sm text-gray-500">인원</span>
                <input
                    type="number"
                    className="w-[50px] border border-gray-200 rounded-md px-2 py-1 text-sm"
                    value={numberOfGuests}
                    min={1}
                    onChange={(e) => onGuestsChange(Number(e.target.value))}
                />
                <span className="text-sm text-gray-500">객실</span>
                <input
                    type="number"
                    className="w-[50px] border border-gray-200 rounded-md px-2 py-1 text-sm"
                    value={numberOfRooms}
                    min={1}
                    onChange={(e) => onRoomsChange(Number(e.target.value))}
                />
            </div>
        </div>
    )
}