import { useState } from "react"
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../store/useAuthStore";
import { signUp } from "../axios/api";
import type { SignUpRequest } from "@/type/auth";
import '@/pages/SignUpPage.css'
import { toast } from "react-toastify";
import { useMutation } from "@tanstack/react-query";
import { useForm } from "react-hook-form";
import NotFoundPage from "./NotFoundPage";

export const SignUpPage = () => {
    const {register, handleSubmit, formState: {errors}} = useForm<SignUpRequest>();
    const navigate = useNavigate();
    const { setAccessToken } = useAuthStore();

    //mutate: api호출 + 로딩상태관리(isPending) + 성공/실패처리
    const {mutate, isPending} = useMutation({
        mutationFn: signUp,
        onSuccess: (res)=>{
            setAccessToken(res.data.accessToken)
            return <NotFoundPage />
        },
        onError : (err: any)=>{
              toast.error(err.response.data.message) 
        }
    })

    return (
        <div className="signup-container">
            <form onSubmit={handleSubmit((data)=>mutate(data))}>
                <div>
                    <label>name</label>
                    <input type="text"
                        {...register("name", {required: "이름을 입력하세요"})} />
                    {errors.name && <p>{errors.name.message}</p>}
                </div>
                <div>
                    <label>email</label>
                    <input type="email"
                       {...register("email", {required: "이메일을 입력하세요"})} />
                </div>
                <div>
                    <label>password</label>
                    <input type="password"
                        {...register("password", {required: "비밀번호를 입력하세요"})} />
                </div>
                <div>
                    <label>phone</label>
                    <input type="tel" maxLength={11}
                        {...register("phone", {required: "번호를 입력하세요"})} />
                </div>
                <button type="submit" disabled={isPending}>
                    {isPending ? "Loading..." : "SignUp"}
                </button>
                <button type="button" onClick={() => navigate("/")}>취소</button>
            </form>
        </div>
    )
}