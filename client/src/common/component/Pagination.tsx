interface PaginationProps {
    page: number,
    totalPages?: number,
    isFirst?: boolean,
    isLast?: boolean,
    onPageChange: (page: number) => void
}

export const Pagination = ({ page, totalPages, isFirst, isLast, onPageChange }: PaginationProps) => {
    return (
        <div>
            <button onClick={() => onPageChange(page - 1)}
                disabled={isFirst}>
                이전
            </button>
            <span>
                {page + 1} / {totalPages ?? 1}
            </span>
            <button onClick={() => onPageChange(page + 1)}
                disabled={isLast}>
                다음
            </button>
        </div>
    )
}