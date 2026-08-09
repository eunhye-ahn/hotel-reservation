interface Pagination_BLOCK_Props {
    page: number,
    totalPages?: number,
    onPageChange: (page: number) => void
}

export const Pagination_BLOCK = ({ page, totalPages = 1, onPageChange }: Pagination_BLOCK_Props) => {
    const BLOCK = 5
    const currentBlock = Math.floor(page / BLOCK)
    let startpage = (currentBlock * BLOCK) + 1
    let endpage = (currentBlock * BLOCK) + BLOCK
    if (endpage > totalPages) {
        endpage = totalPages
    }

    const pageNumbers = []
    const len = endpage - startpage + 1
    for (let i = 0; i < len; i++) {
        pageNumbers.push(startpage + i)
    }

    const hasPrevBlock = startpage > 1
    const hasNextBlock = endpage < totalPages

    return (
        <div className="flex items-center justify-center gap-1 mt-10 mb-6">
            {hasPrevBlock && (
                <button onClick={() => onPageChange(startpage - 2)}
                    className="px-3 py-1.5 text-sm text-gray-600 rounded-lg cursor-pointer hover:bg-gray-100 transition-colors">
                    이전
                </button>
            )}
            {pageNumbers.map((pageNum) => (
                <button key={pageNum}
                    onClick={() => onPageChange(pageNum - 1)}
                    className={`w-8 h-8 text-sm rounded-lg cursor-pointer transition-colors 
                        ${pageNum === page + 1
                            ? "bg-gray-900 text-white font-medium"
                            : "text-gray-600 hover:bg-gray-100"
                        }`}>
                    {pageNum}
                </button>
            ))
            }
            {
                hasNextBlock && (
                    <button onClick={() => onPageChange(endpage + 1)}
                        className="px-3 py-1.5 text-sm text-gray-600 rounded-lg cursor-pointer hover:bg-gray-100 transition-colors">                    다음
                    </button>
                )
            }
        </div >
    )
}