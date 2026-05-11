import { login } from "../axios/api"
import { useNavigate } from "react-router-dom"
import { useAuthStore } from "../store/useAuthStore"
import type { LoginRequest } from "@/type/auth"
import '@/pages/LoginPage.css'
import { toast } from "react-toastify"
import { useMutation } from "@tanstack/react-query"
import { useForm } from "react-hook-form"

export const LoginPage = () => {
    const {register, handleSubmit, formState:{errors}} = useForm<LoginRequest>();
    const navigate = useNavigate();
    const { setAccessToken } = useAuthStore();

    const {mutate, isPending} = useMutation({
        mutationFn: login,
        onSuccess: (res)=>{
                setAccessToken(res.data.accessToken)
                navigate("/")
        },
        onError: (err: any)=>{
            const { code, message } = err.response.data
            if(code === "INVALID_PASSWORD"){
                toast.error(message)
                return
            }
            toast.error("일시적인 오류가 발생했습니다")
        }
    })

    return (
        <div className="login-container">
            <form onSubmit={handleSubmit((data)=>mutate(data))}>
                <div>
                    <label>email</label>
                    <input type="email"
                        {...register("email",{required: "이메일을 입력하세요"})} />
                    {errors.email && <p>{errors.email.message}</p>}
                </div>
                <div>
                    <label>password</label>
                    <input type="password"
                        {...register("password", {required: "비밀번호를 입력하세요"})} />
                    {errors.password && <p>{errors.password.message}</p>}
                </div>
                <button type="submit" disabled={isPending}>
                    {isPending? "Loading..." : "Login"}
                    </button>
                <button type="button" onClick={() => navigate("/signup")}>SignUp</button>
            </form>
        </div>
    )
}