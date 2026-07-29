import { useNavigate } from "react-router";
import type { hotelResponse } from "@/type/hotel"
import { useRecentHotelStore } from "@/store/useRecentHotelStore";
import { useEffect, useRef } from "react";
import { HotelCard } from "./HotelCard";

interface HotelCardListProps {
    data: hotelResponse[];
    fetchNextPage?: () => void;
    hasNextPage?: boolean;
    onRemove?: (hotelId: number) => void
}

export const HotelCardList = ({ data, fetchNextPage, hasNextPage, onRemove }: HotelCardListProps) => {
    const navigate = useNavigate();
    const { saveRecentHotel } = useRecentHotelStore();

    const observerRef = useRef<HTMLDivElement>(null)

    useEffect(() => {
        const target = observerRef.current;
        if (!target) return;
        const observer = new IntersectionObserver((entries) => {
            if (entries[0].isIntersecting && hasNextPage) {
                fetchNextPage?.()
            }
        })
        observer.observe(target)
        return () => observer.disconnect()
    }, [fetchNextPage, hasNextPage])

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
                        onRemove={onRemove ? () => onRemove(hotel.hotelId) : undefined}
                    />
                ))}
            </div>

            {/* 스크롤 감지 타겟  */}
            <div ref={observerRef} style={{ height: "1px" }}></div>
        </div>
    );
}