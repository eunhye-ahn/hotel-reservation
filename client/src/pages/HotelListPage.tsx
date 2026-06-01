import { getHotels } from "@/api/reservation-service";
import type { CursorResponse } from "@/shared/type/hotel";
import { useRegionStore } from "@/store/useRegionStore";
import { useQuery } from "@tanstack/react-query";
import { useEffect } from "react";
import { useNavigate, useParams, useSearchParams } from "react-router";

export function HotelListPage () {
    const [searchParams] = useSearchParams();
    const regionCode = searchParams.get("lDongRegnCd") ?? "";
    const subRegionCode = searchParams.get("lDongSignguCd") ?? "";
    const navigate = useNavigate();
    
    //displayName
    const {displayName, resetRegion} = useRegionStore();

    const {data, isLoading, isError} = useQuery<CursorResponse>({
        queryKey: ["hotels", regionCode, subRegionCode],     //지역바뀌면 자동재조회
        queryFn: () => getHotels(
            regionCode?? "",
            subRegionCode?? "",
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
        <div>
            <h3>{displayName}</h3>
            <hr/>
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