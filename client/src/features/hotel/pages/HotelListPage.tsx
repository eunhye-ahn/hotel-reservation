import { HotelCardList } from "@/features/hotel/component/HotelCardList";
import { SearchFilterBar } from "@/features/hotel/component/SearchFilterBar";
import { useRegionStore } from "@/store/useRegionStore";
import { useEffect } from "react";
import { DateGuestSelector } from "@/features/hotel/component/DateGuestSelector";
import { Modal } from "@/common/component/Modal";
import { FilterSelector } from "@/features/hotel/component/FilterSelector";
import { useHotelListFilter } from "../hooks/useHotelListFilter";
import { useHotelList } from "../hooks/useHotelList";
import { Spinner } from "@/common/component/Spinner";
import { ErrorMessage } from "@/common/component/ErrorMessage";
import { useHotelListModals } from "../hooks/useHotelListModals";

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

    //displayname => 새로고침하면 잊는 이슈
    return (
        <div className="hotel-list-page">
            {displayName
                && <h3 className="font-bold text-lg text-center mt-8">{displayName}</h3>}
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
                        <HotelCardList
                            data={hotels}
                            fetchNextPage={fetchNextPage}
                            hasNextPage={hasNextPage}
                        />
                    )}
        </div>
    )
}