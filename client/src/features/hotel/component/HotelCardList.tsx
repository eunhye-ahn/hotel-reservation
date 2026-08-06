import { useNavigate } from "react-router";
import type { hotelResponse, Page } from "@/api/types/hotel"
import { useRecentHotelStore } from "@/store/useRecentHotelStore";
import { HotelCard } from "./HotelCard";

interface HotelCardListProps {
    data: hotelResponse[] | Page<hotelResponse> | undefined,
    onRemove?: (hotelId: number) => void
}

export const HotelCardList = ({ data, onRemove }: HotelCardListProps) => {
    const navigate = useNavigate();
    const { saveRecentHotel } = useRecentHotelStore();

    const hotels = data
        ? Array.isArray(data)
            ? data
            : data.content
        : []

    return (
        <div className="page-container">
            {hotels?.length === 0 && <p>호텔이 없습니다</p>}
            <div className="grid grid-cols-4 gap-2">
                {hotels?.map((hotel) => (
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
                        onRemove={onRemove ? () => onRemove(hotel.hotelId) : undefined}
                    />
                ))}
            </div>
        </div>
    );
}