import { useNavigate, useSearchParams } from "react-router";

export default function PaymentFail () {
    const [searchParams] = useSearchParams();
    const navigate = useNavigate();

    const code = searchParams.get("code")
    const message = searchParams.get("message")

    return(
        <div>
            <h2>결제에 실패했습니다</h2>
            <p>{message}</p>
            <p>에러코드 : {code}</p>
        </div>
    )
}