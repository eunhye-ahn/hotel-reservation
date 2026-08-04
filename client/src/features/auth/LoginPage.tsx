import { useNavigate } from "react-router-dom"
import { useAuthStore } from "@/store/useAuthStore"
import type { CustomJwtPayLoad, LoginRequest } from "@/api/types/auth"
import { toast } from "react-toastify"
import { useMutation } from "@tanstack/react-query"
import { useForm } from "react-hook-form"
import { login } from "@/api/api"
import { jwtDecode } from "jwt-decode";


export const LoginPage = () => {
    const { register, handleSubmit, setError, formState: { errors } } = useForm<LoginRequest>();
    const navigate = useNavigate();
    const { setAccessToken, setRole, role } = useAuthStore();

    const { mutate, isPending } = useMutation({
        mutationFn: login,
        onSuccess: (res) => {
            const newAccessToken = res.data.accessToken
            setAccessToken(newAccessToken)
            const deocded = jwtDecode<CustomJwtPayLoad>(newAccessToken!);
            setRole(deocded.role)
            navigate("/")
            console.log(role)
        },
        onError: (err: any) => {
            const { code, message } = err.response.data
            if (code === "INVALID_PASSWORD") {
                setError('root', { message })
                return
            }
            toast.error("일시적인 오류가 발생했습니다")
        }
    })
    return (
        <div className="min-h-screen flex items-center justify-center">
            <form
                className="w-full max-w-sm border border-gray-200 rounded-xl p-8"
                onSubmit={handleSubmit((data) => mutate(data))}>
                <h1 className="text-xl font-bold mb-6 text-center">로그인</h1>
                <div className="mb-4">
                    <label className="block text-sm text-gray-500 mb-1">이메일</label>
                    <input type="email"
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm"
                        {...register("email", { required: "이메일을 입력하세요" })} />
                    {errors.email && <p className="text-xs text-red-500 mt-1 text-center">{errors.email.message}</p>}
                </div>
                <div>
                    <label>password</label>
                    <input type="password"
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm"
                        {...register("password", { required: "비밀번호를 입력하세요" })} />
                    {errors.password && <p className="text-xs text-red-500 mt-1 text-center">{errors.password.message}</p>}
                    {errors.root && <p className="text-xs text-red-500 mt-1 text-center">{errors.root.message}</p>}
                </div>
                <button type="submit"
                    className="w-full mt-3 bg-gray-900 text-white rounded-lg py-2 font-medium cursor-pointer hover:bg-gray-800 disabled:opacity-40"
                    disabled={isPending}>
                    {isPending ? "Loading..." : "Login"}
                </button>
                <button type="button"
                    className="w-full mt-0.5 bg-gray-900 text-white rounded-lg py-2 font-medium cursor-pointer hover:bg-gray-800 disabled:opacity-40"
                    onClick={() => navigate("/signup")}>SignUp</button>
            </form>
        </div>
    )
}