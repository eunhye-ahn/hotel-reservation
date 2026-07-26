import { HotelCard } from "@/features/hotel/component/HotelCard";
import { SearchFilterBar } from "@/features/hotel/component/SearchFilterBar";
import { useRegionStore } from "@/store/useRegionStore";
import { useEffect } from "react";
import '@/css/HotelListPage.css'
import '@/css/HotelCard.css';
import { DateGuestSelector } from "@/common/component/DateGuestSelector";
import { Modal } from "@/common/component/Modal";
import { FilterSelector } from "@/common/component/FilterSelector";
import { useHotelListFilter } from "./hooks/useHotelListFilter";
import { useHotelList } from "./hooks/useHotelList";
import { Spinner } from "@/common/component/Spinner";
import { ErrorMessage } from "@/common/component/ErrorMessage";
import { useHotelListModals } from "./hooks/useHotelListModals";

export function HotelListPage() {
    const { filter } = useHotelListFilter()

    const { isDateOpen, setIsDateOpen, isFilterOpen, setIsFilterOpen, setIsSortOpen } = useHotelListModals()

    const { displayName, resetRegion } = useRegionStore();

    const { data, isLoading, isError, fetchNextPage, hasNextPage } = useHotelList(filter)
    const hotels = data?.pages.flatMap(page => page.content) ?? [];


    useEffect(() => {
        return () => {
            resetRegion()
        }
    }, []);

    return (
        <div className="hotel-list-page">
            {displayName && <h3>{displayName}</h3>}
            <SearchFilterBar
                checkIn={filter.checkIn}
                checkOut={filter.checkOut}
                guestToReserve={filter.numberOfGuests}
                roomToReserve={filter.numberOfRooms}
                onDateClick={() => setIsDateOpen(true)}
                onFilterClick={() => setIsFilterOpen(true)}
                onSortClick={() => setIsSortOpen(true)}
            />
            {isDateOpen && (
                <Modal isOpen={isDateOpen} onClose={() => setIsDateOpen(false)} title="날짜,인원 선택">
                    <DateGuestSelector onClose={() => setIsDateOpen(false)} />
                </Modal>
            )}
            {isFilterOpen && (
                <Modal isOpen={isFilterOpen} onClose={() => setIsFilterOpen(false)} title="필터">
                    <FilterSelector onClose={() => setIsFilterOpen(false)} />
                </Modal>
            )}
            {isLoading ? <Spinner />
                : isError ? <ErrorMessage />
                    : (
                        <HotelCard
                            data={hotels}
                            fetchNextPage={fetchNextPage}
                            hasNextPage={hasNextPage}
                        />
                    )}
        </div>
    )
}