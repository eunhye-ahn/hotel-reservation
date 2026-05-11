import { getHotels } from "@/axios/api";
import {type hotelResponse, type Page } from "@/type/hotel";
import '@/pages/MainPage.css';
import { useNavigate } from "react-router";
import { useQuery } from "@tanstack/react-query";

//호텔정보페이지
export const MainPage = () => {
    const navigate = useNavigate();

    /**
     * useQuery vs useMutation
     * get          post,put,delete,patch
     * 데이터조회       데이터변경
     * 컴포넌트 마운트 시 자동          직접 mutate()호출
     * 캐싱 있음            없음
     */

    //useQuery: api 자동호출, isLoading/isError 상태 자동관리 /캐싱키
    const {data, isLoading, isError} = useQuery<Page<hotelResponse>>({
        queryKey: ["hotels"],
        queryFn: () => getHotels().then((res)=>res.data)
    })

    if(isLoading) return <p>loading...</p>
    if(isError) return <p>호텔 정보를 불러오는데 실패했습니다</p>

    return (
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
    )
}