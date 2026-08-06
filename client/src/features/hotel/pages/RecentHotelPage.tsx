import { HotelCardList } from "@/features/hotel/component/HotelCardList";
import { useRecentHotelStore } from "@/store/useRecentHotelStore"

export function RecentHotelPage() {
    const { recentHotels, removeRecentHotel } = useRecentHotelStore();

    return (
        <div>
            <HotelCardList
                data={recentHotels}
                onRemove={removeRecentHotel}
            />
        </div>
    )
}