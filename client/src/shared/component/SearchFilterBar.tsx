import { CalendarIcon, FilterIcon, SortAscIcon } from "lucide-react";
import '@/shared/component/SearchFilterBar.css'

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
        <div className="filter-bar">
            <button className="filter-btn" onClick={onDateClick}>
                <CalendarIcon />
                {checkIn}~{checkOut} · {guestToReserve}명
            </button>
            <button className="filter-btn" onClick={onFilterClick}>
                <FilterIcon />
                필터
            </button>
            <button className="filter-btn" onClick={onSortClick}>
                <SortAscIcon />
                정렬
            </button>
        </div>
    )
}