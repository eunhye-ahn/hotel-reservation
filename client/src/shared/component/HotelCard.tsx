import { useNavigate } from "react-router";
import type { CursorResponse } from "../type/hotel"

interface HotelCardProps {
    data: CursorResponse | undefined;
}

export const HotelCard = ({data}: HotelCardProps) => {
const navigate = useNavigate();

    return (
        <div className="hotel-list">
            {data?.content.length === 0 && <p>호텔이 없습니다</p>}
            {data?.content.map((hotel) => (
                <div
                    key={hotel.hotelId}
                    className="hotel-card"
                    onClick={() => navigate(`/hotels/${hotel.hotelId}`)}
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