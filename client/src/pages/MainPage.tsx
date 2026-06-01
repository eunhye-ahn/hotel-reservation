import '@/pages/MainPage.css';
import { useNavigate } from "react-router";
import { useQuery } from "@tanstack/react-query";
import { getHotels } from "@/api/reservation-service";
import type { CursorResponse, hotelResponse, Page } from '@/shared/type/hotel';
import { useEffect, useState } from 'react';
import { getDisplayName, REGIONS, type Region, type SubRegion } from '@/constants/Region';
import { RegionSelector } from '@/shared/component/RegionSelector';
import { Modal } from '@/shared/component/Modal';
import { useRegionStore } from '@/store/useRegionStore';

//호텔정보페이지
export const MainPage = () => {
    const navigate = useNavigate();
    const {setRegion, regionCode, subRegionCode, displayName, resetRegion} = useRegionStore();
    const [isOpen, setIsOpen] = useState(false);

    /**
     * useQuery vs useMutation
     * get          post,put,delete,patch
     * 데이터조회       데이터변경
     * 컴포넌트 마운트 시 자동          직접 mutate()호출
     * 캐싱 있음            없음
     */

    //useQuery: api 자동호출, isLoading/isError 상태 자동관리 /캐싱키
    const {data, isLoading, isError} = useQuery<CursorResponse>({
        queryKey: ["hotels", regionCode, subRegionCode],     //지역바뀌면 자동재조회
        queryFn: () => getHotels(
           regionCode,
            subRegionCode,
            0
        ).then((res)=>res.data)
    })

    const handleSelect = (region : Region, subRegion?: SubRegion) => {
        const newRegionCode = region.code;
        const newSubRegionCode = subRegion?.code;
        setRegion(getDisplayName(newRegionCode, newSubRegionCode), newRegionCode, newSubRegionCode);
        setIsOpen(false);

        navigate(`/hotels/list?lDongRegnCd=${newRegionCode}${newSubRegionCode ? `&lDongSignguCd=${newSubRegionCode}` : ""}`);
    }

    if(isLoading) return <p>loading...</p>
    if(isError) return <p>호텔 정보를 불러오는데 실패했습니다</p>

    return (
        <div>
            <div className="search-wrap">
                <h3 className='search-title'>어디로 갈까요?</h3>
                <div className='search-row'>
                    <button onClick={()=> setIsOpen(true)}
                        className='region-btn'>
                        {regionCode && !isOpen ? displayName : "지역선택" }
                    </button>
                    <button  className="nearby-btn">내주변</button>
                </div>
            </div>

            {isOpen && (
                <div>
                    <Modal  isOpen={isOpen} onClose={() => setIsOpen(false)} title="지역 선택">
                        <RegionSelector onSelect={handleSelect}/>
                    </Modal>
                </div>
            )}

        <div className="hotel-list">
            {data?.content.length === 0 && <p>호텔이 없습니다</p>}
            {data?.content.map((hotel) => (
                <div 
                key={hotel.hotelId} className="hotel-card"
                onClick={() => navigate(`/hotels/${hotel.hotelId}`)} >
                    <img className="hotel-img" src={hotel.imageUrl} />
                    <p className="hotel-name">{hotel.name}</p>
                    <p className="hotel-address">{hotel.address}</p>
                    <p className="hotel-checkin">숙박 {hotel.checkInTime.substring(0,5)}~</p>
                    <div className="hotel-price-row">
                        {hotel.maxRate && hotel.demandRate ? (
                            <>
                                <span className="hotel-original">{hotel.maxRate.toLocaleString()}</span>
                                <span className="hotel-discount">{hotel.discountRate}%</span>
                            </>
                        ) : (
                            <span>요금 준비 중</span>
                        )}
                    </div>
                    <p className="hotel-demand">
                        {hotel.demandRate ? `${hotel.demandRate.toLocaleString()}원` : ""}
                    </p>
                </div>
            ))}
        </div>
        </div>
    )
}