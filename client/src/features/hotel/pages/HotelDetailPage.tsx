import { useState } from "react"
import { useNavigate, useParams } from "react-router";
import { toast } from "react-toastify";
import { Map } from "@/common/component/Map";
import { useWishList } from "@/features/hotel/hooks/useWishList";
import { useCreateReservation } from "@/features/reservation/hooks/useCreateReservation";
import { addDays, format } from "date-fns";
import { useHotelDetail } from "../hooks/useHotelDetail";
import { Spinner } from "@/common/component/Spinner";
import { RoomSearchBar } from "../component/RoomSearchBar";
import { HotelDetailInfo } from "../component/HotelDetailInfo";
import { RoomCard } from "../component/RoomCard";
import { getErrorMessage } from "@/api/errorHelpers";

export const HotelDetailPage = () => {
    const today = format(new Date(), 'yyyy-MM-dd')
    const tomorrow = format(addDays(new Date(), 1), 'yyyy-MM-dd')

    const navigate = useNavigate();

    const { hotelId } = useParams();
    const [startDate, setStartDate] = useState(today);
    const [endDate, setEndDate] = useState(tomorrow);
    const [numberOfRooms, setNumberOfRooms] = useState(1);
    const [numberOfGuests, setNumberOfGuests] = useState(1);

    const { data, isLoading, isError, error } = useHotelDetail({ hotelId, startDate, endDate, numberOfRooms, numberOfGuests })

    const { isWished, handleWish } = useWishList(Number(data?.hotelId));

    const { handleReservation, isPending } = useCreateReservation({
        hotelId,
        data,
        startDate,
        endDate,
        numberOfGuests,
        numberOfRooms
    });


    if (isLoading) return <Spinner />
    if (isError) {
        toast.error(getErrorMessage(error))
        navigate("/")
        return null
    }

    return (
        <>
            <div className="detail-container">

                <RoomSearchBar
                    startDate={startDate}
                    endDate={endDate}
                    numberOfRooms={numberOfRooms}
                    numberOfGuests={numberOfGuests}
                    onStartDateChange={setStartDate}
                    onEndDateChange={setEndDate}
                    onRoomsChange={setNumberOfRooms}
                    onGuestsChange={setNumberOfGuests}
                />
                <HotelDetailInfo
                    imageUrl={data?.imageUrl}
                    hotelName={data?.hotelName}
                    address={data?.address}
                    isWished={isWished}
                    onWishClick={() => handleWish(data?.hotelId)}
                />
                <div className="font-bold text-lg mt-8 mb-4">객실선택</div>
                {data?.roomTypes.length === 0 && <p>객실이 없습니다</p>}
                {data?.roomTypes.map((roomType) => (
                    <RoomCard
                        roomTypeId={roomType.roomTypeId}
                        imageUrl={roomType.imageUrl}
                        name={roomType.name}
                        checkInTime={data.checkInTime}
                        checkOutTime={data.checkOutTime}
                        availableCount={roomType.availableCount}
                        maxRate={roomType.maxRate}
                        discountRate={roomType.discountRate}
                        demandRate={roomType.demandRate}
                        isPending={isPending}
                        onReserve={handleReservation}
                    />
                ))}

                <div>
                    <div className="font-bold text-lg mt-8 mb-4">위치</div>
                    <Map hotelName={data?.hotelName ?? ""} hotelAddress={data?.address ?? ""} />
                </div>
            </div>
        </>
    )
}