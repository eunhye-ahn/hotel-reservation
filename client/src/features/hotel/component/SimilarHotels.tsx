import { getSimilarHotel } from "@/api/api";
import { useRecentHotelStore } from "@/store/useRecentHotelStore"
import { useQuery } from "@tanstack/react-query"
import { useState } from "react";
import { Swiper, SwiperSlide } from 'swiper/react';
import { Navigation, Pagination, Autoplay } from 'swiper/modules';
import { HotelCard } from "@/features/hotel/component/HotelCard";
import { useNavigate } from "react-router";
import "swiper/css/bundle"

export const SimilarHotels = () => {
    const [page, setPage] = useState(0);

    const { recentHotels, saveRecentHotel } = useRecentHotelStore();
    const navigate = useNavigate();

    const { data, isLoading } = useQuery({
        queryKey: ['similar-hotels', recentHotels[0]?.hotelId, page],
        queryFn: () => getSimilarHotel(recentHotels[0].hotelId, page),
        enabled: recentHotels.length > 0
    })

    if (recentHotels.length === 0) return null;


    return (
        <div style={{ marginBlock: '3rem' }}>
            <h2 className="font-bold text-gray-800 text-lg mb-2">회원님을 위한 맞춤 추천</h2>
            {isLoading
                ? <p>loading...</p>
                : (
                    <Swiper
                        modules={[Navigation, Pagination, Autoplay]}
                        navigation
                        pagination={{ clickable: true }}
                        spaceBetween={20}
                        slidesPerView={3}
                        autoplay={{
                            delay: 3000,
                            disableOnInteraction: false,
                        }}
                    >
                        {(data?.data.content ?? []).map((hotel) => (
                            <SwiperSlide key={hotel.hotelId}>
                                <HotelCard
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
                                        });
                                        navigate(`/hotels/${hotel.hotelId}`);
                                    }}
                                />
                            </SwiperSlide>
                        ))}
                    </Swiper>
                )}
        </div>
    )
}