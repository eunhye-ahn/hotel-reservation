import { CalendarIcon, FilterIcon, SortAscIcon } from "lucide-react";

interface SearchFilterBarProps {
    checkIn: string,
    checkOut: string,
    guestToReserve: number,
    roomToReserve: number,
    onDateClick: () => void,
    onFilterClick: () => void,
    onSortClick: () => void;
}


export const SearchFilterBar = ({
    checkIn, checkOut, guestToReserve, roomToReserve,
    onDateClick, onFilterClick, onSortClick
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
            <button className="flex text-sm items-center gap-1.5 border rounded-full border-gray-500 px-4 py-2 hover:bg-gray-200 cursoer-pointer"
                onClick={onSortClick}>
                <SortAscIcon />
                정렬
            </button>
        </div>
    )
}