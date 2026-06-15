import { create } from "zustand";
import { persist } from "zustand/middleware";

/**
 * API응답 타입과 스토리지 저장용 타입 분리해두면
 * API가 바뀌어도 store는 안전하다
 */
type RecentHotel = {
    hotelId: number,
    name: string,
    maxRate: number,
    demandRate: number,
    discountRate: number,
    checkInTime: string,
    address: string,
    imageUrl: string
}

interface RecentHotelState {
    recentHotels: RecentHotel[];
    saveRecentHotel: (hotel: RecentHotel) => void;
    removeRecentHotel: (hotelId: number) => void;
}

export const useRecentHotelStore = create<RecentHotelState>()(
    persist(
        (set, get)=>({
            recentHotels: [],

            //중복제거+맨앞에 추가+최대10개
            saveRecentHotel: (hotel)=>{
                const filtered= get().recentHotels.filter(h=>h.hotelId !== hotel.hotelId)
                const updated = [hotel, ...filtered].slice(0,10)
                set({recentHotels: updated})
            },

            removeRecentHotel: (hotelId) => {
                const updated = get().recentHotels.filter(h=> h.hotelId !== hotelId)
                set({recentHotels: updated})
            },
            
        }),
        {name: "recent-hotel"}
    )
)