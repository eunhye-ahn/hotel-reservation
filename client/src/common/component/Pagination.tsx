interface PaginationProps {
    page: number,
    totalPages?: number,
    isFirst?: boolean,
    isLast?: boolean,
    onPageChange: (page: number) => void
}

export const Pagination = ({ page, totalPages, isFirst, isLast, onPageChange }: PaginationProps) => {
    return (

        <div className="flex items-center justify-center gap-1 mt-6 mb-6">

            {!isFirst &&
                <button onClick={() => onPageChange(page - 1)}
                    className="px-1 py-0.5 bg-gray-200 mx-1 border border-gray-300 hover:bg-gray-400">
                    이전
                </button>
            }
            {totalPages! > 0 &&
                <span>
                    {page + 1} / {totalPages ?? 1}
                </span>
            }
            {!isLast &&
                <button onClick={() => onPageChange(page + 1)}
                    className="px-1 py-0.5 bg-gray-200 mx-1 border border-gray-300 hover:bg-gray-400">
                    다음
                </button>
            }
        </div>
    )
}