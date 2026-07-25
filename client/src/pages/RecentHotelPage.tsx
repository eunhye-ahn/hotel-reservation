import { HotelCard } from "@/features/hotel/component/HotelCard";
import { useRecentHotelStore } from "@/store/useRecentHotelStore"

export function RecentHotelPage() {
    const { recentHotels, removeRecentHotel } = useRecentHotelStore();

    return (
        <div>
            <HotelCard
                data={recentHotels}
                onRemove={removeRecentHotel}
            />
        </div>
    )
}