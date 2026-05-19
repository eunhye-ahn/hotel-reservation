import { confirmPayment, getReservationKey } from "@/api/payment-service";
import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router"

export default function PaymentSuccess () {
    const [searchParams] = useSearchParams();
    const orderId = searchParams.get("orderId")
    const paymentKey = searchParams.get("paymentKey")
    const amount = searchParams.get("amount")

    
    const navigate = useNavigate();

    useEffect(()=>{
        if (!orderId || !paymentKey || !amount) return;

        confirmPayment({
            orderId,
            paymentKey,
            amount : Number(amount)
        })
        .then((res)=>navigate(`/reservations/${res.data.reservationKey}`))
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