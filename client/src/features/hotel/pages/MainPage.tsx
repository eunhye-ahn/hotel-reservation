
import { useNavigate } from "react-router";
import { useState } from 'react';
import { getDisplayName, type Region, type SubRegion } from '@/api/types/Region';
import { RegionSelector } from '@/common/component/RegionSelector';
import { Modal } from '@/common/component/Modal';
import { useRegionStore } from '@/store/useRegionStore';
import { SimilarHotels } from '@/features/hotel/component/SimilarHotels';
import { HeroSection } from "@/common/component/HeroSection";
import { PopularHotelList } from "../component/PopularHotelList";

//호텔정보페이지
export const MainPage = () => {
    const navigate = useNavigate();
    const { setRegion, saveRecentRegion } = useRegionStore();
    const [isOpen, setIsOpen] = useState(false);

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

            </div>

            <PopularHotelList />
        </div>
    )
}