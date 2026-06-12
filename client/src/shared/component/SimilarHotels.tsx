import { getSimilarHotel } from "@/api/reservation-service";
import { useRecentHotelStore } from "@/store/useRecentHotelStore"
import { useQuery } from "@tanstack/react-query"
import { useState } from "react";
import { HotelCard } from "./HotelCard";

export const SimilarHotels = () => {
    const {recentHotels} = useRecentHotelStore();
    const [page, setPage] = useState(0);

    const {data, isLoading} = useQuery({
        queryKey: ['similar-hotels', recentHotels[0]?.hotelId, page],
        queryFn: ()=> getSimilarHotel(recentHotels[0].hotelId, page),
        enabled : recentHotels.length > 0
    })

    if (recentHotels.length === 0) return null;

    return(
        <div className="similar-section">
            <h2 className="similar-title">이런 상품은 어떠세요?</h2>
            <p className="similar-subtitle">최근 본 상품과 비슷한 상품</p>
            {isLoading 
                ? <p>loading...</p>
                : <HotelCard data={data?.data.content ?? []}/>
            }

            <div  className="similar-pagination">
                <button disabled={data?.data.first} onClick={()=>setPage(p=>p-1)}>&laquo;</button>
                <span>{page+1} / {data?.data.totalPages}</span>
                <button disabled={data?.data.last} onClick={()=>setPage(p=>p+1)}>&raquo;</button>
            </div>
        </div>
    )
}

/**
 *     content: T[],
    totalElements: number,
    totalPages: number,
    number: number,
    size: number,
    first: boolean,
    last: boolean
 */