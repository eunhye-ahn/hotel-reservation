export const ErrorFallback = () => {
    return (
        <div className="flex flex-col items-center justify-center h-screen gap-4">
            <p className="text-lg font-semibold text-gray-800">
                문제가 발생했습니다
            </p>
            <p className="text-sm text-gray-500">
                페이지를 새로고침해도 계속되면 잠시 후 다시 시도해주세요
            </p>
            <button
                className="px-4 py-2 bg-gray-900 text-white rounded-lg text-sm"
                onClick={() => window.location.href = "/"}
            >
                홈으로 이동
            </button>
        </div>
    )
}