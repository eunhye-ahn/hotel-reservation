import type { hotelResponse } from "@/type/hotel";

interface HotelCardProps {
    hotel: hotelResponse;
    onClick: () => void;
    onRemove?: () => void;
}

export const HotelCard = ({ hotel, onClick, onRemove }: HotelCardProps) => {
    return (
        <div className="cursor-pointer" onClick={onClick}>
            {onRemove && (
                <button className="" onClick={(e) => {
                    e.stopPropagation();
                    onRemove()
                }}>
                    x
                </button>
            )}
            <img className="w-full aspect-[4/3] object-cover" src={hotel.imageUrl} />
            <p className="font-semibold text-sm mt-3 mb-1">{hotel.name}</p>
            <p className="text-xs text-gray-500">{hotel.address}</p>
            <p className="text-xs text-gray-500 my-1">숙박 {hotel.checkInTime.substring(0, 5)}~</p>
            <div className="text-right px-3">
                <div className="flex items-center gap-2 mt-1 justify-end">
                    {hotel.maxRate && hotel.demandRate ? (
                        <>
                            <p className="text-xs text-gray-500 line-through">{hotel.maxRate.toLocaleString()}</p>
                            <p className="text-red-500 font-bold text-sm">{hotel.discountRate}%</p>
                        </>
                    ) : (
                        <span>요금 준비 중</span>
                    )}
                </div>
                <p className="font-bold text-base mt-1">
                    {hotel.demandRate ? `${hotel.demandRate.toLocaleString()}원` : ""}
                </p>
            </div>
        </div>
    )
}