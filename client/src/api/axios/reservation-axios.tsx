import axios from "axios";
import { useAuthStore } from "../../store/useAuthStore";
import { toast } from 'react-toastify';
import { reissue } from "../reservation-service";

export const reservationApi = axios.create({
    baseURL: "http://localhost:8000/api/v1",
    headers: {
        'Content-Type': 'application/json'
    },
    withCredentials: true
})
/**
 * config: 요청 설정 객체 - url, headers, method, data 등
 * error: 요청 설정 실패하면 에러처리 
 */

reservationApi.interceptors.request.use(
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
reservationApi.interceptors.response.use(
    (response) => response,

    async (error) => {
        if (!error.response) {
            // 네트워크 에러 -> undefined일 경우 타입에러 방지
            //토스트안줘도될려나?
            return Promise.reject(error);
        }

        const status = error.response.status;

        if (status === 401 && !error.config._retry) {
            error.config._retry = true;
            try {
                const res = await reissue();
                useAuthStore.getState().setAccessToken(res.data.accessToken)
                return reservationApi(error.config)
            } catch (e) {
                useAuthStore.getState().setAccessToken(null);
                window.location.href = "/login";
                return Promise.reject(e);
            }
        }
        if(status === 403){
            toast.error('접근 권한이 없습니다');
        }
        if (status === 500) {
            toast.error('일시적인 오류가 발생했습니다. 다시 시도해주세요');
        }
        return Promise.reject(error);
    }
)