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
        <div className="search-bar">
            <input
                type="date"
                value={startDate}
                onChange={(e) => onStartDateChange(e.target.value)}
            />
            <span>~</span>
            <input
                type="date"
                value={endDate}
                onChange={(e) => onEndDateChange(e.target.value)}
            />
            <div className="guest-select">
                <span>인원</span>
                <input
                    type="number"
                    value={numberOfGuests}
                    min={1}
                    onChange={(e) => onGuestsChange(Number(e.target.value))}
                />
                <span>객실</span>
                <input
                    type="number"
                    value={numberOfRooms}
                    min={1}
                    onChange={(e) => onRoomsChange(Number(e.target.value))}
                />
            </div>
        </div>
    )
}