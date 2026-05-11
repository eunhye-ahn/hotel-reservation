import { getHotelDetail } from "@/axios/api";
import type { HotelDetailResponse } from "@/type/hotel";
import { useState } from "react"
import { useNavigate, useParams } from "react-router";
import '@/pages/HotelDetailPage.css';
import { useQuery } from "@tanstack/react-query";
import { toast } from "react-toastify";
import NotFoundPage from "./NotFoundPage";

export const HotelDetailPage = () => {
    const today = new Date().toISOString().split('T')[0];
    const tomorrow = new Date(Date.now() + 86400000).toISOString().split('T')[0];
    const navigate = useNavigate();

    const { hotelId } = useParams();
    const [startDate, setStartDate] = useState(today);
    const [endDate, setEndDate] = useState(tomorrow);
    const [numberOfRooms, setNumberOfRooms] = useState(1);
    const [numberOfGuests, setNumberOfGuests] = useState(1);

    const {data, isLoading, isError, error} = useQuery<HotelDetailResponse>({
        queryKey: ["hotelDetails", hotelId],    //호텔id별로 캐시관리
        queryFn: () => getHotelDetail(Number(hotelId)).then((res)=>res.data)
    });

    if(isLoading) return <p>Loading...</p>
    if(isError){
        const { code, message } = (error as any).response.data;
    
        if(code === "HOTEL_NOT_FOUND"){
            return <NotFoundPage />
        }
        if(code === "RATE_NOT_FOUND" || code === "ROOM_INVENTORY_NOT_FOUND"){
            toast.error(message)
            navigate("/")
            return null
        }

        toast.error("일시적인 오류가 발생했습니다")
        navigate("/")
        return null
    }
    return (
        <div className="detail-container">
            <div className="search-bar">
                <input
                    type="date"
                    value={startDate}
                    onChange={(e) => setStartDate(e.target.value)}
                />
                <span>~</span>
                <input
                    type="date"
                    value={endDate}
                    onChange={(e) => setEndDate(e.target.value)}
                />
                <div className="guest-select">
                    <span>인원</span>
                    <input
                        type="number"
                        value={numberOfGuests}
                        min={1}
                        onChange={(e) => setNumberOfGuests(Number(e.target.value))}
                    />
                    <span>객실</span>
                    <input
                        type="number"
                        value={numberOfRooms}
                        min={1}
                        onChange={(e) => setNumberOfRooms(Number(e.target.value))}
                    />
                </div>
            </div>
            <div className="hotel-info">
                <img src={data?.imageUrl} />
                <div className="hotel-info-text">
                    <p>{data?.hotelName}</p>
                    <p>{data?.address}</p>
                </div>
            </div>
            <div className="room-select-title">객실선택</div>
            {data?.roomTypes.map((roomType) => (
                <div className="room-card" key={roomType.roomTypeId}>
                    <img src={roomType.imageUrl} />
                    <div className="room-card-info">
                        <p>{roomType.name}</p>
                        <p>숙박 {data.checkInTime.substring(0, 5)}~{data.checkOutTime.substring(0, 5)}</p>
                        <p>남은객실 {roomType.availableCount}개</p>
                    </div>
                    <div className="room-card-price">
                        <div className="hotel-price-row">
                            <span className="hotel-original">{roomType.maxRate.toLocaleString()}</span>
                            <span className="hotel-discount">{roomType.discountRate}%</span>
                        </div>
                        <p className="hotel-demand">{roomType.demandRate.toLocaleString()}원</p>
                        <button onClick={() => navigate(`/hotels/${hotelId}/rooms/${roomType.roomTypeId}`, {
                            state: {
                                hotelName: data.hotelName,
                                hotelAddress: data.address,
                                roomTypeName: roomType.name,
                                imageUrl: roomType.imageUrl,
                                checkInTime: data.checkInTime,
                                checkOutTime: data.checkOutTime,
                                startDate,
                                endDate,
                                numberOfRooms,
                                numberOfGuests,
                            }
                        })}>예약하기</button>
                    </div>
                </div>
            ))}
        </div>
    )
}