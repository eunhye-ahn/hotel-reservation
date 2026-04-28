
import type { AccessTokenResponse, LoginRequest, SignUpRequest } from "@/type/auth";
import { api } from "./axiosInstance"
import axios from "axios";

export const login = (request: LoginRequest) => {
    return api.post<AccessTokenResponse>("/auth/login", request);
}

export const logout = () => {
    return api.post<void>("/auth/logout");
}

//새로고침,at만료 시(401반환 시 -인증실패) 호출 - 무한루프 방지
export const reissue = () => {
    return axios.post<AccessTokenResponse>("http://localhost:8080/api/auth/reissue", null, {
        withCredentials: true
    });
}

export const signUp = (request: SignUpRequest) => {
    return api.post<AccessTokenResponse>("/auth/signUp", request);
}