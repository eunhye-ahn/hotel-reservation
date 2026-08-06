import type { CustomErrorResponse } from './types/error'
import { AxiosError } from "axios"
import * as Sentry from '@sentry/react'
import { toast } from "react-toastify"
import { getSeverityLevel } from "./CustomSeverityLevel"
import { SentryNetworkError } from "./SentryNetworkError"

export const getErrorCode = (error: unknown): string | undefined => {
    if (isApiError(error)) return error.response?.data.code
}
export const getErrorMessage = (error: unknown): string => {
    if (isApiError(error) && error.response?.data?.message) {
        return error.response.data.message
    }
    return "오류가 발생했습니다"
}

export const handleDefenseError = (err: unknown, invalidate: () => void) => {
    if (isApiError(err)) {
        Sentry.withScope((scope) => {
            scope.setLevel(getSeverityLevel(getErrorCode(err)))
            Sentry.captureException(new SentryNetworkError(err))
        })
    }

    toast.error(getErrorMessage(err))
    invalidate()
}

const isApiError = (error: unknown): error is AxiosError<CustomErrorResponse> => {
    return !!(error as AxiosError)?.isAxiosError
}
