import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../../store/useAuthStore";
import type { CustomJwtPayLoad, SignUpRequest } from "@/api/types/auth";
import { toast } from "react-toastify";
import { useMutation } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import { signUp } from "@/api/api";
import { jwtDecode } from "jwt-decode";
import { getErrorCode, getErrorMessage } from "@/api/errorHelpers";

interface SignUpFormValues extends SignUpRequest {
    passwordConfirm: string;
}

export const SignUpPage = () => {
    const { register, reset, watch, setValue, setFocus, handleSubmit, setError, formState: { errors } } = useForm<SignUpFormValues>();
    const navigate = useNavigate();
    const { setAccessToken, setRole } = useAuthStore();

    const { mutate, isPending } = useMutation({
        mutationFn: signUp,
        onSuccess: (res) => {
            const newAccessToken = res.data.accessToken
            setAccessToken(newAccessToken)
            const deocded = jwtDecode<CustomJwtPayLoad>(newAccessToken!);
            setRole(deocded.role)
            navigate("/")
        },
        onError: (err: any) => {
            const code = getErrorCode(err)
            const message = getErrorMessage(err)
            if (code === "DUPLICATE_EMAIL") {
                setError('root', { message })
                setValue('email', '')
                setFocus('email')
                return
            }
            toast.error("일시적인 오류가 발생했습니다")
            reset()
            setFocus('email')
        }
    })

    const onSubmit = (data: SignUpFormValues) => {
        const { passwordConfirm, ...signUpData } = data
        mutate(signUpData)
    }

    return (
        <div className="min-h-screen flex items-center justify-center">
            <form
                className="w-full max-w-sm border border-gray-200 rounded-xl p-8"
                onSubmit={handleSubmit(onSubmit)}>
                <h1 className="text-xl font-bold mb-6 text-center">회원가입</h1>

                <div className="mb-4">
                    <label className="block text-sm text-gray-500 mb-1">이름</label>
                    <input type="text"
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm"
                        {...register("name", { required: "이름을 입력하세요" })} />
                    {errors.name && <p className="text-xs text-red-500 mt-1 text-center">{errors.name.message}</p>}
                    {errors.root && <p className="text-xs text-red-500 mt-1 text-center">{errors.root.message}</p>}
                </div>

                <div className="mb-4">
                    <label className="block text-sm text-gray-500 mb-1">이메일</label>
                    <input type="email"
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm"
                        {...register("email", { required: "이메일을 입력하세요" })} />
                    {errors.email && <p className="text-xs text-red-500 mt-1 text-center">{errors.email.message}</p>}
                </div>

                <div className="mb-4">
                    <label className="block text-sm text-gray-500 mb-1">비밀번호</label>
                    <input type="password"
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm"
                        {...register("password", {
                            required: "비밀번호를 입력하세요",
                            pattern: {
                                value: /^(?=.*[A-Za-z])(?=.*\d)[A-Za-z\d]{8,}$/,
                                message: "비밀번호 8자 이상, 영문+숫자 조합이어야 합니다"
                            }
                        })} />
                    {errors.password && <p className="text-xs text-red-500 mt-1 text-center">{errors.password.message}</p>}
                </div>

                <div className="mb-4">
                    <label className="block text-sm text-gray-500 mb-1">비밀번호 확인</label>
                    <input type="password"
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm"
                        {...register("passwordConfirm", {
                            required: "비밀번호를 다시 입력하세요",
                            validate: (value) =>
                                value === watch("password") || "비밀번호가 일치하지 않습니다"
                        })} />
                    {errors.passwordConfirm && <p className="text-xs text-red-500 mt-1 text-center">{errors.passwordConfirm.message}</p>}
                </div>

                <div className="mb-4">
                    <label className="block text-sm text-gray-500 mb-1">전화번호</label>
                    <input type="tel" maxLength={11}
                        placeholder="010-0000-0000"
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm"
                        {...register("phone", {
                            required: "번호를 입력하세요",
                            pattern: {
                                value: /^010\d{8}$/,
                                message: "전화번호 형식이 올바르지 않습니다"
                            }
                        })} />
                    {errors.phone && <p className="text-xs text-red-500 mt-1 text-center">{errors.phone.message}</p>}
                </div>

                <button type="submit"
                    className="w-full mt-3 bg-gray-900 text-white rounded-lg py-2 font-medium cursor-pointer hover:bg-gray-800 disabled:opacity-40"
                    disabled={isPending}>
                    {isPending ? "Loading..." : "등록"}
                </button>
                <button type="button"
                    className="w-full mt-0.5 border border-gray-300 text-gray-700 rounded-lg py-2 font-medium cursor-pointer hover:bg-gray-50"
                    onClick={() => navigate("/")}>취소</button>
            </form>
        </div>
    )
}