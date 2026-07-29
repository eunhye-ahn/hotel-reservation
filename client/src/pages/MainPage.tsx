
import { useNavigate } from "react-router";
import { useInfiniteQuery } from "@tanstack/react-query";
import { getHotels } from "@/api/api";
import type { CursorResponse } from '@/type/hotel';
import { useState } from 'react';
import { getDisplayName, type Region, type SubRegion } from '@/type/Region';
import { RegionSelector } from '@/common/component/RegionSelector';
import { Modal } from '@/common/component/Modal';
import { useRegionStore } from '@/store/useRegionStore';
import { HotelCardList } from '@/features/hotel/component/HotelCardList';
import { SimilarHotels } from '@/features/hotel/component/SimilarHotels';
import { HeroSection } from "@/common/component/HeroSection";

//호텔정보페이지
export const MainPage = () => {
    const navigate = useNavigate();
    const { setRegion, regionCode, subRegionCode, displayName, resetRegion, recentRegions, saveRecentRegion, removeRecentRegion } = useRegionStore();
    const [isOpen, setIsOpen] = useState(false);

    const { data, isLoading, isError, fetchNextPage, hasNextPage } = useInfiniteQuery<CursorResponse>({
        queryKey: ["hotels"],
        queryFn: ({ pageParam }) => getHotels(pageParam as number | undefined)
            .then((res) => res.data),
        initialPageParam: undefined,
        getNextPageParam: (lastPage) => lastPage.nextCursor ?? undefined
    })
    const hotels = data?.pages.flatMap(page => page.content) ?? [];

    const handleSelect = (region: Region, subRegion?: SubRegion) => {
        const newRegionCode = region.code;
        const newSubRegionCode = subRegion?.code;
        setRegion(getDisplayName(newRegionCode, newSubRegionCode), newRegionCode, newSubRegionCode);
        saveRecentRegion(region, subRegion)
        setIsOpen(false);

        const today = new Date().toLocaleDateString('en-CA')
        const tomorrow = new Date(Date.now() + 86400000).toLocaleDateString('en-CA');
        navigate(`/hotels/list?lDongRegnCd=${newRegionCode}${newSubRegionCode ? `&lDongSignguCd=${newSubRegionCode}` : ""}&startDate=${today}&endDate=${tomorrow}&numberOfGuests=3&numberOfRooms=1`);
    }

    if (isLoading) return <p>loading...</p>
    if (isError) return <p>호텔 정보를 불러오는데 실패했습니다</p>

    return (
        <div>
            <HeroSection onRegionClick={() => setIsOpen(true)} />
            <div className="page-container">
                <div className="search-wrap" style={{
                    maxWidth: '600px',
                    marginInline: 'auto',
                    position: 'relative',
                    backgroundColor: 'black',
                    color: 'white',
                    zIndex: 10
                }}>

                </div>


                {isOpen && (
                    <div>
                        <Modal isOpen={isOpen} onClose={() => setIsOpen(false)} title="지역 선택">
                            <RegionSelector onSelect={handleSelect} />
                        </Modal>
                    </div>
                )}

                <SimilarHotels />
                <HotelCardList
                    data={hotels}
                    fetchNextPage={fetchNextPage}
                    hasNextPage={hasNextPage}
                />
            </div>
        </div>
    )
}