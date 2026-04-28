import { AccessTokenResponse, LoginRequest, SignUpRequest } from "../type/auth"
import { api } from "./axiosInstance"

export const login = (request: LoginRequest) => {
    return api.post<AccessTokenResponse>("/auth/login", request);
}

export const logout = () => {
    return api.post<void>("/auth/logout"); 
}

export const reissue = () => {
    return api.post<AccessTokenResponse>("/auth/reissue");
}

export const signUp = (request: SignUpRequest) => {
    return api.post<AccessTokenResponse>("/auth/signUp", request);
}