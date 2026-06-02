import { getHotels } from "@/api/reservation-service";
import { HotelCard } from "@/shared/component/HotelCard";
import { SearchFilterBar } from "@/shared/component/SearchFilterBar";
import type { CursorResponse } from "@/shared/type/hotel";
import { useRegionStore } from "@/store/useRegionStore";
import { useQuery } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router";
import '@/pages/HotelListPage.css'
import '@/shared/component/HotelCard.css';
import { DateGuestSelector } from "@/shared/component/DateGuestSelector";
import { Modal } from "@/shared/component/Modal";

export function HotelListPage () {
    const [searchParams, setSearchParams] = useSearchParams();
    const regionCode = searchParams.get("lDongRegnCd") ?? "";
    const subRegionCode = searchParams.get("lDongSignguCd") ?? "";

    const today = new Date().toLocaleDateString('en-CA')
    const tomorrow = new Date(Date.now() + 86400000).toLocaleDateString('en-CA');

    const checkIn = searchParams.get("startDate") ?? today;
    const checkOut = searchParams.get("endDate") ?? tomorrow;
    const guestToReserve = Number(searchParams.get("numberOfGuests") ?? 3);
    const roomToReserve = Number(searchParams.get("roomToReserve") ?? 1);

    const [isDateOpen, setIsDateOpen] = useState<boolean>(false);
    const [isFilterOpen, setIsFilterOpen] = useState<boolean>(false);
    const [isSortOpen, setIsSortOpen] = useState<boolean>(false);

    const navigate = useNavigate();
    
    //displayName
    const {displayName, resetRegion} = useRegionStore();

    const {data, isLoading, isError} = useQuery<CursorResponse>({
        queryKey: ["hotels", regionCode, subRegionCode, checkIn, checkOut, guestToReserve],     //지역바뀌면 자동재조회
        queryFn: () => getHotels(
            regionCode?? "",
            subRegionCode?? "",
            checkIn,
            checkOut,
            guestToReserve,
            0
        ).then((res)=>res.data)
    });

    useEffect(()=>{
        return () => {
            resetRegion()
        }
    },[]);

    if(isLoading) return <p>loading...</p>
    if(isError) return <p>호텔 정보를 불러오는데 실패했습니다</p>

    return(
        <div className="hotel-list-page">
            <h3>{displayName}</h3>
            <SearchFilterBar 
                checkIn={checkIn}
                checkOut={checkOut}
                guestToReserve={guestToReserve}
                roomToReserve={roomToReserve}
                onDateClick={()=>setIsDateOpen(true)}
                onFilterClick={()=>setIsFilterOpen(true)}
                onSortClick={()=>setIsSortOpen(true)}
            />
            {isDateOpen && (
                <Modal isOpen={isDateOpen} onClose={()=>setIsDateOpen(false)} title="날짜,인원 선택">
                    <DateGuestSelector onClose={()=>setIsDateOpen(false)}/>
                </Modal>
            )}
            <HotelCard data={data} />
        </div>
    )
}