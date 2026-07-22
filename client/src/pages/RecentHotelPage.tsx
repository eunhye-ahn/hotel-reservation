import { HotelCard } from "@/component/HotelCard";
import { useRecentHotelStore } from "@/store/useRecentHotelStore"

export function RecentHotelPage(){
    const {recentHotels, removeRecentHotel} = useRecentHotelStore();

    return(
        <div>
            <HotelCard 
            data={recentHotels}
            onRemove={removeRecentHotel}
            />
        </div>
    )
}