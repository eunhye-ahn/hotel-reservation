const RESERVE_LABEL: Record<string, string> = {
    BEFORE_USE: '이용전',
    AFTER_USE: '이용후',
    CANCELED: '취소',
    EXPIRED: '만료'
}

const RESERVE_COLOR: Record<string, string> = {
    BEFORE_USE: 'text-green-500',
    AFTER_USE: 'text-orange-500',
    CANCELED: 'text-red-500',
    EXPIRED: 'text-gray-500'
}

export const ReservationStatusBadge = ({ status }: { status: string }) => {

    return (
        <span className={`font-semibold ${RESERVE_COLOR[status]}`}>
            {RESERVE_LABEL[status]}
        </span>
    )
} 