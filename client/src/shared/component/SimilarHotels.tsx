import { getSimilarHotel } from "@/api/reservation-service";
import { useRecentHotelStore } from "@/store/useRecentHotelStore"
import { useQuery } from "@tanstack/react-query"
import { useState } from "react";

export const SimilarHotels = () => {
    const {recentHotels} = useRecentHotelStore();
    const [page, setPage] = useState(0);

    const {data: similarHotels, isLoading: similarHotelsLoading} = useQuery({
        queryKey: ['similar-hotels', recentHotels[0].hotelId]
        queryFn: ()=> getSimilarHotel(recentHotels[0].hotelId, page),
        enabled : recentHotels.length > 0
    })

    if (recentHotels.length === 0) return null;

    return(
        <div>
            <h2>이런 상품은 어떠세요?</h2>
            <p>최근 본 상품과 비슷한 상품</p>
            {}
        </div>
    )
}