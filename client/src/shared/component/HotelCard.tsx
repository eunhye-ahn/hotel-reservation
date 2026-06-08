import { useNavigate } from "react-router";
import type { CursorResponse, hotelResponse } from "../type/hotel"
import { useRecentHotelStore } from "@/store/useRecentHotelStore";

interface HotelCardProps {
    data: hotelResponse[];
    fetchNextPage: ()=> void;
    hasNextPage: boolean;
}

export const HotelCard = ({data}: HotelCardProps) => {
const navigate = useNavigate();
const {saveRecentHotel} = useRecentHotelStore();

    return (
        <div className="hotel-list">
            {data?.length === 0 && <p>호텔이 없습니다</p>}
            {data?.map((hotel) => (
                <div
                    key={hotel.hotelId}
                    className="hotel-card"
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
                >
                    <img className="hotel-img" src={hotel.imageUrl} />
                    <p className="hotel-name">{hotel.name}</p>
                    <p className="hotel-address">{hotel.address}</p>
                    <p className="hotel-checkin">숙박 {hotel.checkInTime.substring(0, 5)}~</p>
                    <div className="hotel-price-row">
                        {hotel.maxRate && hotel.demandRate ? (
                            <>
                                <span className="hotel-original">{hotel.maxRate.toLocaleString()}</span>
                                <span className="hotel-discount">{hotel.discountRate}%</span>
                            </>
                        ) : (
                            <span>요금 준비 중</span>
                        )}
                    </div>
                    <p className="hotel-demand">
                        {hotel.demandRate ? `${hotel.demandRate.toLocaleString()}원` : ""}
                    </p>
                </div>
            ))}
        </div>
    );
}