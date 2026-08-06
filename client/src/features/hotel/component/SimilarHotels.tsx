import { useRecentHotelStore } from "@/store/useRecentHotelStore"
import { Swiper, SwiperSlide } from 'swiper/react';
import { Navigation, Pagination, Autoplay } from 'swiper/modules';
import { HotelCard } from "@/features/hotel/component/HotelCard";
import { useNavigate } from "react-router";
import "swiper/css/bundle"
import { useSimilarHotelList } from "../hooks/useSimilarHotelList";
import { Spinner } from "@/common/component/Spinner";
import { ErrorMessage } from "@/common/component/ErrorMessage";

export const SimilarHotels = () => {

    const { recentHotels, saveRecentHotel } = useRecentHotelStore();
    const navigate = useNavigate();

    const { data, isLoading, isError } = useSimilarHotelList({ recentHotels })

    if (recentHotels.length === 0) return null;


    return (
        < div style={{ marginBlock: '3rem' }
        }>
            <h2 className="font-bold text-gray-800 text-lg mb-2">회원님을 위한 맞춤 추천</h2>
            {
                isLoading
                    ? <Spinner />
                    : isError ? <ErrorMessage />
                        : (
                            <Swiper
                                modules={[Navigation, Pagination, Autoplay]}
                                navigation
                                pagination={{ clickable: true }}
                                spaceBetween={15}
                                slidesPerView={3}
                                slidesPerGroup={3}
                                autoplay={{
                                    delay: 3000,
                                    disableOnInteraction: false,
                                }}
                            >

                                {data?.map((hotel) => (
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
                        )
            }

        </div >

    )
}