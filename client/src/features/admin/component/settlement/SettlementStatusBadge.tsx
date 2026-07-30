const STATUS_LABEL: Record<string, string> = {
    PENDING: '진행중',
    COMPLETED: '완료',
    FAILED: '실패'
}

const STATUS_COLOR: Record<string, string> = {
    PENDING: 'text-gray-500',
    COMPLETED: 'text-green-500',
    FAILED: 'text-red-500'
}

export const SettlementStatusBadge = ({ status }: { status: string }) => {
    return (
        <span className={`font-semibold ${STATUS_COLOR[status]}`}>
            {STATUS_LABEL[status]}
        </span>
    )
}