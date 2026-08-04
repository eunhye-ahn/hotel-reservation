import type { SeverityLevel } from "@sentry/react";

//즉시대응필요
const FATAL_CODES = [
    "PAYMENT_AMOUNT_MISMATCH",
    "PAYMENT_PROCESSING_FAILED",
]

//데이텅정합성 오류
const ERROR_CODES = [
    "REFUND_FAILED",
    "PAYMENT_CANCEL_FAILED",
    "OPTIMISTIC_LOCK_CONFLICT"
]

//재시도로 가능한 것
const WARNING_CODES = [
    "INVALID_DATE_RANGE",
    "EXCEED_MAX_OCCUPANCY",
    "MISSING_IDEMPOTENCY_KEY",
    "WALLET_NOT_FOUND",
    "INVALID_SETTLEMENT_STATUS",
    "HASH_GENERATION_FAILED",
    "IDEMPOTENCY_FAILED",
    "IDEMPOTENCY_UNKNOWN",
    "IDEMPOTENCY_NOT_FOUND",
    "IDEMPOTENCY_USER_MISMATCH",
    "IDEMPOTENCY_REQUEST_MISMATCH",
    "ROOM_TYPE_MISMATCH",
    "ROOM_NOT_USABLE",
    "ROOM_ALREADY_OCCUPIED",
    "CANNOT_UNASSIGN_ROOM"
]

export function getSeverityLevel(code: string | undefined): SeverityLevel {
    if (!code) return "error"
    if (FATAL_CODES.includes(code)) return "fatal"
    if (ERROR_CODES.includes(code)) return "error"
    if (WARNING_CODES.includes(code)) return "warning"
    return "error"
}