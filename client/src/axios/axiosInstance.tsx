import axios from "axios";
import { useAuthStore } from "../../../client/src/store/useAuthStore";
import { reissue } from "./api";

export const api = axios.create({
    baseURL: "http://localhost:8080/api",
    headers: {
        'Content-Type': 'application/json'
    },
    withCredentials: true
})
/**
 * config: 요청 설정 객체 - url, headers, method, data 등
 * error: 요청 설정 실패하면 에러처리 
 */

api.interceptors.request.use(
    (config) => {
        const token = useAuthStore.getState().accessToken;

        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config;
    },
    (error) => Promise.reject(error)
)
/**
 * response : 서버 성공 응답 객체 - data, status, headers, config
 * error : 서버가 에러 응답 객체 
 */
api.interceptors.response.use(
    (response) => response,

    async (error) => {
        if (error.response.status == 401 && !error.config._retry) {
            error.config._retry = true;
            try {
                const res = await reissue();
                useAuthStore.getState().setAccessToken(res.data.accessToken)
                return api(error.config)
            } catch (e) {
                useAuthStore.getState().setAccessToken(null);
                window.location.href = "/login";
                return Promise.reject(e);
            }
        }
        return Promise.reject(error);
    }
)