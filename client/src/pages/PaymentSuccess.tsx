import { confirmPayment } from "@/api/payment-service";
import { getReservationStatus } from "@/api/reservation-service";
import { useQuery } from "@tanstack/react-query";
import { useEffect, useRef, useState } from "react";
import { useNavigate, useSearchParams } from "react-router"

export default function PaymentSuccess () {
    const [searchParams] = useSearchParams();
    const orderId = searchParams.get("orderId")
    const paymentKey = searchParams.get("paymentKey")
    const amount = searchParams.get("amount")

    const [confirmed, setConfirmed] = useState(false);
    const reservationKeyRef = useRef<string | null>(null); 
    const navigate = useNavigate();

    const {data} = useQuery({
        queryKey: ["paymentStuats", orderId],
        queryFn: () => getReservationStatus(reservationKeyRef.current!).then(res => res.data),
        refetchInterval: 2000,  //2초마다 자동 재요청(폴링)
        enabled: confirmed && !!orderId,
    })

    useEffect(()=>{
        console.log(data)
        if(data === "PAID"){
            navigate(`/reservations/${reservationKeyRef.current}`)
        }
    },[data])

    useEffect(()=>{
        if (!orderId || !paymentKey || !amount) return;

        confirmPayment({
            orderId,
            paymentKey,
            amount : Number(amount)
        })
        .then((res)=>{
            reservationKeyRef.current = res.data.reservationKey;
            console.log( reservationKeyRef.current);
            setConfirmed(true);
        }
        )
        .catch(()=>{
             alert("결제 승인에 실패했습니다.");
            navigate("/");
        })
    },[])

    return(
        <div>
            <p>결제처리중입니다</p>
        </div>
    )
}