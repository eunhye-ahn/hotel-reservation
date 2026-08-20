import dayjs from "dayjs"

interface RoomSearchBarProps {
    startDate: string,
    endDate: string,
    numberOfGuests: number,
    numberOfRooms: number,
    today: string,
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
    today,
    onStartDateChange,
    onEndDateChange,
    onRoomsChange,
    onGuestsChange
}: RoomSearchBarProps) => {
    return (
        <div className="flex items-center gap-3 p-4 border border-gray-200 rounded-xl my-6">
            <input
                type="date"
                className="border-none outline-none text-sm font-semibold"
                value={startDate}
                onChange={(e) => onStartDateChange(e.target.value)}
                min={today}
            />
            <span className="text-gray-400 px-2 py-1 bg-black text-white border-radius rounded-sm mx-3">
                {dayjs(endDate).diff(dayjs(startDate), 'day')}박
            </span>
            <input
                type="date"
                className="border-none outline-none text-sm font-semibold"
                value={endDate}
                onChange={(e) => onEndDateChange(e.target.value)}
                min={dayjs(startDate).add(1,'day').format('YYYY-MM-DD')}
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