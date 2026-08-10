import { CalendarIcon, FilterIcon } from "lucide-react";

interface SearchFilterBarProps {
    checkIn: string,
    checkOut: string,
    guestToReserve: number,
    roomToReserve: number,
    onDateClick: () => void,
    onFilterClick: () => void
}


export const SearchFilterBar = ({
    checkIn, checkOut, guestToReserve, roomToReserve,
    onDateClick, onFilterClick
}: SearchFilterBarProps) => {
    return (
        <div className="flex gap-10 py-4 justify-center">
            <button className="flex text-sm items-center gap-1.5 border rounded-full border-gray-500 px-4 py-2 hover:bg-gray-200 cursoer-pointer"
                onClick={onDateClick}>
                <CalendarIcon />
                {checkIn}~{checkOut} · {guestToReserve}명
            </button>
            <button className="flex text-sm items-center gap-1.5 border rounded-full border-gray-500 px-4 py-2 hover:bg-gray-200 cursoer-pointer"
                onClick={onFilterClick}>
                <FilterIcon />
                필터
            </button>
        </div>
    )
}