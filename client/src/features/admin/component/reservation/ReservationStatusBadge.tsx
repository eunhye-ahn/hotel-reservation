export const RESERVE_LABEL: Record<string, string> = {
    BEFORE_USE: '이용전',
    AFTER_USE: '이용후',
    CANCELED: '취소',
    EXPIRED: '만료',
    CANCELED_PENDING: '취소대기',
}

export const RESERVE_COLOR: Record<string, string> = {
    BEFORE_USE: 'text-blue-500',
    AFTER_USE: 'text-green-500',
    CANCELED: 'text-red-500',
    EXPIRED: 'text-gray-500',
    CANCELED_PENDING: 'text-yellow-500',
}

export const ReservationStatusBadge = ({ status }: { status: string }) => {

    return (
        <span className={`font-semibold ${RESERVE_COLOR[status]}`}>
            {RESERVE_LABEL[status]}
        </span>
    )
} 