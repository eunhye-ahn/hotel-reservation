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
        <div className="items-center gap-6">
            <img className="w-full h-[55vh] object-cover" src={imageUrl} />
            <div className="flex mb-10 mt-3">
                <div className="flex-1">
                    <p className="font-bold text-xl mb-2">{hotelName}</p>
                    <p className="text-sm text-gray-400">{address}</p>
                </div>
                <button className="" onClick={onWishClick}>
                    <Heart size={24} fill={isWished ? "red" : "none"} color={isWished ? "red" : "#9ca3af"} />
                </button>
            </div>
        </div>
    )
}