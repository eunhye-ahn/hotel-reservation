
const PAY_LABEL: Record<string, string> = {
    NOT_STARTED: '결제대기',
    SUCCESS: '결제완료',
    FAILED: '결제실패',
    CANCELED: '결제취소'
}

const PAY_COLOR: Record<string, string> = {
    NOT_STARTED: 'text-gray-400',
    SUCCESS: 'text-green-500',
    FAILED: 'text-red-500',
    CANCELED: 'text-gray-500'
}

export const PaymentStatusBadge = ({ status }: { status: string }) => {
    return (
        <span className={`font-semibold ${PAY_COLOR[status]}`}>
            {PAY_LABEL[status]}
        </span>
    )
}