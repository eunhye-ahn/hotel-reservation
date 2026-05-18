import { confirmPayment } from "@/api/payment-service";
import { useMutation } from "@tanstack/react-query";
import { useEffect } from "react";
import { useNavigate, useSearchParams } from "react-router"

export default function PaymentSuccess () {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    const {mutate} =useMutation({
        mutationFn: confirmPayment,
        onSuccess: (res)=> {
            navigate(`/reservations/${res.data.reservationKey}`);
        },
        onError: () => {
            alert("결제 승인에 실패했습니다")
            navigate("/")
        }
    })

    useEffect(()=>{
        //쿼리파라미터
        const paymentKey = searchParams.get("paymentKey");
        const orderId = searchParams.get("orderId");
        const amount = Number(searchParams.get("amount"));

        //필수파라미터 검증
        if(!paymentKey || !orderId || !amount){
            alert("잘못된 접근입니다")
            navigate("/");
            return;
        }

        //api호출
        mutate({paymentKey, orderId, amount})
    },[]);

    return(
        <div>
            <p>결제처리중입니다</p>
        </div>
    )
}