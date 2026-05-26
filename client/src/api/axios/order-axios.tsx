// axios/payment-service/index.ts
import axios from "axios";
import { useAuthStore } from "../../store/useAuthStore";
import { toast } from 'react-toastify';

export const orderApi = axios.create({
    baseURL: "http://localhost:8000/api/v1",
    headers: {
        'Content-Type': 'application/json'
    },
    withCredentials: true
})

orderApi.interceptors.request.use(
    (config) => {
        const token = useAuthStore.getState().accessToken;
        if (token) {
            config.headers.Authorization = `Bearer ${token}`
        }
        return config;
    },
    (error) => Promise.reject(error)
)

orderApi.interceptors.response.use(
    (response) => response,
    async (error) => {
        if (!error.response) {
            return Promise.reject(error);
        }
        const status = error.response.status;
        if(status === 403){
            toast.error('접근 권한이 없습니다');
        }
        if (status === 500) {
            toast.error('일시적인 오류가 발생했습니다. 다시 시도해주세요');
        }
        return Promise.reject(error);
    }
)