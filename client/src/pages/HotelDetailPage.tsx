import type { HotelDetailResponse } from "@/shared/type/hotel";
import {  useRef, useState } from "react"
import { useNavigate, useParams } from "react-router";
import '@/pages/HotelDetailPage.css';
import { useMutation, useQuery } from "@tanstack/react-query";
import { toast } from "react-toastify";
import NotFoundPage from "./NotFoundPage";
import { createReservation, getHotelDetail } from "@/api/reservation-service";
import { Map } from "@/shared/component/Map";

export const HotelDetailPage = () => {
    //한국 기준 오늘날짜 설정 -date기본값
    const today = new Date().toLocaleDateString('en-CA')
    const tomorrow = new Date(Date.now() + 86400000).toLocaleDateString('en-CA');

    const navigate = useNavigate();

    const { hotelId } = useParams();
    const [startDate, setStartDate] = useState(today);
    const [endDate, setEndDate] = useState(tomorrow);
    const [numberOfRooms, setNumberOfRooms] = useState(1);
    const [numberOfGuests, setNumberOfGuests] = useState(1);
    const selectedRoomTypeIdRef = useRef<number|null>(null);

    const reservationKey = useRef(crypto.randomUUID());

    const {data, isLoading, isError, error} = useQuery<HotelDetailResponse>({
        queryKey: ["hotelDetails", hotelId, startDate, endDate, numberOfRooms, numberOfGuests],    //호텔id별로 캐시관리
        queryFn: () => getHotelDetail(Number(hotelId), startDate, endDate, numberOfRooms, numberOfGuests).then((res)=>res.data)
    });
    
        const {mutate : createReservationMutate, isPending} = useMutation({
        mutationFn: createReservation,
        onSuccess: (res)=>{
            const {reservationKey, orderId} =res.data;
                const roomType = data?.roomTypes.find(r => r.roomTypeId === selectedRoomTypeIdRef.current);
               navigate(`/reservations/${reservationKey}/reservation-info`, {
                state: {
                    orderId,
                    reservationKey,
                    hotelName: data?.hotelName,
                    hotelAddress: data?.address,
                    roomTypeName: roomType?.name,
                    imageUrl: roomType?.imageUrl,
                    checkInTime: data?.checkInTime,
                    checkOutTime: data?.checkOutTime,
                    startDate,
                    endDate,
                    numberOfRooms,
                    numberOfGuests
                }
            }); 
        },
        onError: (err: any) => {
            const message = err.response.data.message;
            const code = err.response.data.code;

            switch(code){
                case 'PRICE_TOKEN_EXPIRED':
                case 'PRICE_TOKEN_NOT_FOUND':
                case 'RESERVATION_CONFLICT':
                case 'IDEMPOTENCY_NOT_FOUND':
                case 'IDEMPOTENCY_FAILED':
                case 'IDEMPOTENCY_REQUEST_MISMATCH':
                case 'IDEMPOTENCY_USER_MISMATCH':
                case 'HASH_GENERATION_FAILED':
                case 'IDEMPOTENCY_UNKNOWN':
                case 'IDEMPOTENCY_PROCESSING':
                case 'INVALID_INPUT':
                    toast.error(message);
                    navigate(`/hotels/${hotelId}`);
                    break;
                default:
                    toast.error("일시적인 오류가 발생했습니다")
                    navigate(`/hotels/${hotelId}`)   
            }
        }
    })

    const handleReservation = (roomTypeId: number) => {
        if (!data) return;
        selectedRoomTypeIdRef.current = roomTypeId;
        createReservationMutate({
                reservationKey: reservationKey.current,
                hotelId: Number(hotelId),
                roomTypeId,
                startDate,
                endDate,
                numberOfGuests,
                numberOfRooms
            });
    }

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
                        <button onClick={()=>handleReservation(roomType.roomTypeId)} disabled={isPending}>
                        {isPending ? "Loading..." : "예약하기"}
                            </button>
                    </div>
                </div>
            ))}

            <div>
                 <div className="room-select-title">위치</div>
                <Map hotelName={data?.hotelName ?? ""} hotelAddress={data?.address ?? ""} />
            </div>
        </div>
    )
}