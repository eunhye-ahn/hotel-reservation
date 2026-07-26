import { Heart } from "lucide-react"

interface HotelDetailInfoProps {
    imageUrl?: string,
    hotelName?: string,
    address?: string,
    isWished: boolean,
    onWishClick: () => void
}

export const HotelDetailInfo = ({ imageUrl, hotelName, address, isWished, onWishClick }: HotelDetailInfoProps) => {
    return (
        <div className="hotel-info">
            <img src={imageUrl} />
            <div className="hotel-info-text">
                <p>{hotelName}</p>
                <p>{address}</p>
            </div>
            <button onClick={() => onWishClick}>
                <Heart size={24} fill={isWished ? "red" : "none"} color={isWished ? "red" : "white"} />
            </button>
        </div>
    )
}