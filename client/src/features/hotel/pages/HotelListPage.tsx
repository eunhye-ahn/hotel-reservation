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
import { Pagination_BLOCK } from "@/common/component/Pagination_BLOCK";
import { useSearchParams } from "react-router"

export function HotelListPage() {
    const { filter } = useHotelListFilter()
    const [searchParams, setSearchParams] = useSearchParams()
    const { isDateOpen, setIsDateOpen, isFilterOpen, setIsFilterOpen, setIsSortOpen } = useHotelListModals()

    const { displayName, resetRegion } = useRegionStore();



    const page = Number(searchParams.get('page') ?? 0)

    const { data, isLoading, isError } = useHotelList(filter, page)
    const handlePageChange = (newPage: number) => {
        setSearchParams(prev => {
            const params = new URLSearchParams(prev)
            params.set('page', String(newPage))
            return params
        })
    }

    useEffect(() => {
        return () => {
            resetRegion()
        }
    }, []);

    //displayname => 새로고침하면 잊는 이슈
    return (
        <div className="page-container">
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
                        <div className="w-80px">
                            <HotelCardList
                                data={data}
                            />
                            <Pagination_BLOCK
                                page={page}
                                totalPages={data?.totalPages}
                                onPageChange={handlePageChange}
                            />
                        </div>
                    )}
        </div>
    )
}