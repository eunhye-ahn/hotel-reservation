//sentry 적용시에만 axiosError 형식을 바꾸기 위해 
//xiosError를 감싸는 새인스턴스를 생성한다

import type { CustomErrorResponse } from "./types/error";
import { AxiosError } from "axios";

export class SentryNetworkError extends AxiosError {
    private static generateName(error: AxiosError<CustomErrorResponse>): string {
        const status = error.response?.data.status ?? "Unknown"
        const code = error.response?.data.code ?? "Unknown"
        const baseURL = error.config?.baseURL ?? ""
        const path = (error.config?.url ?? "").split("?")[0]
        const replacePathParams = path.replace(/\/\d+(?=\/|$)/g, '/{id}')

        return `[${status}][${code}] - ${baseURL}${replacePathParams}`
    }

    constructor(error: AxiosError<CustomErrorResponse>) {
        super(error.message, error.code, error.config, error.request, error.response)
        this.name = SentryNetworkError.generateName(error)
    }
}