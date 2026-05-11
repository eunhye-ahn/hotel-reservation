import { createReservation, getRoomTypeForReservation } from "@/axios/api";
import type { ReservationRequest, RoomTypeReservationResponse } from "@/type/reservation";
import { useLocation, useNavigate, useParams } from "react-router"
import '@/pages/ReservationPage.css';
import { toast } from "react-toastify";
import { useMutation, useQuery } from "@tanstack/react-query";

export const ReservationPage = () => {
    const { hotelId, roomTypeId } = useParams();
    const location = useLocation();
    const state = location.state;
    const navigate = useNavigate();

    const {data, isLoading, isError, error} = useQuery<RoomTypeReservationResponse>({
        queryKey: ["reservationForm", hotelId, roomTypeId, state.startDate, state.endDate, state.numberOfRooms],
        queryFn: () => getRoomTypeForReservation(Number(hotelId), Number(roomTypeId), state.startDate, state.endDate, state.numberOfRooms)
                        .then((res)=>res.data)
    })

    const {mutate, isPending} = useMutation({
        mutationFn: createReservation,
        onSuccess: (res)=>{
            navigate(`/reservations/${res.data}`)
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
                    toast.error(message);
                    navigate(`/hotels/${hotelId}`);
                    break;
                default:
                    toast.error("일시적인 오류가 발생했습니다")
                    navigate(`/hotels/${hotelId}`)   
            }
        }
    })

    const handleReservation = () => {
        if (!data) return;
        const reservationData: ReservationRequest = {
            reservationKey: crypto.randomUUID(),
            priceToken : data.priceToken,
            hotelId: Number(hotelId),
            roomTypeId: Number(roomTypeId),
            startDate: state.startDate,
            endDate: state.endDate,
            numberOfGuests: state.numberOfGuests,
            numberOfRooms: state.numberOfRooms
        };
        mutate(reservationData)
    }

        if(isLoading) return <p>Loading...</p>
    if(isError) {
        const {code,message} = (error as any).response.data;
        if(code === "ROOM_TYPE_NOT_FOUND"){
            navigate("/404")
            return null;
        }
        if(code === "RATE_NOT_FOUND" || code === "ROOM_INVENTORY_NOT_FOUND"){
            toast.error(message)
            navigate(`/hotels/${hotelId}`)
            return null;
        }
        toast.error("일시적인 오류가 발생했습니다")
        navigate(`/hotels/${hotelId}`)
        return null;
    }

return(
    <div className="reservation-container">
        <div className="reservation-notice">이 가격의 객실이 {data?.availableCount}개 남았어요</div>
        <div className="reservation-content">
            <div className="reservation-left">
                <div className="hotel-info">
                    <p className="hotel-name">{state.hotelName}</p>
                    <p className="hotel-address">{state.hotelAddress}</p>
                </div>
                <p className="room-type-name">{state.roomTypeName}</p>
                <div className="room-info">
                    <img className="room-image" src={state.imageUrl} />
                    <div className="room-price">
                        <p className="room-demand">{data?.demandRate.toLocaleString()}</p>
                    </div>
                </div>
                <div className="checkin-info">
                    <div className="checkin">
                        <p>체크인</p>
                        <p>{state.startDate}</p>
                        <p>{state.checkInTime?.substring(0, 5)}</p>
                    </div>
                    <div className="nights">1박</div>
                    <div className="checkout">
                        <p>체크아웃</p>
                        <p>{state.endDate}</p>
                        <p>{state.checkOutTime?.substring(0, 5)}</p>
                    </div>
                </div>
            </div>
            <div className="reservation-right">
                <div className="payment-box">
                    <div className="payment-row">
                        <span>결제금액</span>
                        <span>{data?.totalPrice.toLocaleString()}</span>
                    </div>
                    <button className="payment-button" onClick={handleReservation} disabled={isPending}>
                        {isPending ? "Loading..." : "결제하기"}
                    </button>
                </div>
            </div>
        </div>
    </div>
)}