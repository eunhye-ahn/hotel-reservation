import { Spinner } from "@/common/component/Spinner"
import { usePopularHotel } from "../hooks/usePopularHotel"
import { ErrorMessage } from "@/common/component/ErrorMessage"
import { HotelCard } from "./HotelCard"
import { useRecentHotelStore } from "@/store/useRecentHotelStore"
import { useNavigate } from "react-router"

export const PopularHotelList = () => {
    const { data, isLoading, isError } = usePopularHotel()
    const { saveRecentHotel } = useRecentHotelStore()
    const navigate = useNavigate()

    if (isLoading) return <Spinner />
    if (isError) return <ErrorMessage />

    return (
        <div className="page-container">
            {data?.length === 0 && <p>호텔이 없습니다</p>}
            <div className="grid grid-cols-4 gap-2">
                {data?.map((hotel) => (
                    <HotelCard
                        key={hotel.hotelId}
                        hotel={hotel}
                        onClick={() => {
                            saveRecentHotel({
                                hotelId: hotel.hotelId,
                                name: hotel.name,
                                imageUrl: hotel.imageUrl,
                                address: hotel.address,
                                checkInTime: hotel.checkInTime,
                                maxRate: hotel.maxRate,
                                demandRate: hotel.demandRate,
                                discountRate: hotel.discountRate,
                            })
                            navigate(`/hotels/${hotel.hotelId}`)
                        }}
                    />
                ))}
            </div>
        </div>
    );
}