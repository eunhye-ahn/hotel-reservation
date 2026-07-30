export const AdminDashBoard = () => {
    return (
        <div>
            <div className="grid grid-cols-5 gap-1 mb-8">
                <div className="border border-red-500 p-5 bg-white">
                    <p className="text-sm text-gray-500 mb-2">오늘 체크인</p>
                    <p className="text-2xl font-bold text-red-500"></p>
                </div>
                <div className="border border-gray-500 p-5 bg-white">
                    <p className="text-sm text-gray-500 mb-2">미배정 예약</p>
                    <p className="text-2xl font-bold text-gray-500"></p>
                </div>
                <div className="border border-gray-500 p-5 bg-white">
                    <p className="text-sm text-gray-500 mb-2">오늘 결제금액</p>
                    <p className="text-2xl font-bold text-gray-500"></p>
                </div>
                <div className="border border-gray-500 p-5 bg-white">
                    <p className="text-sm text-gray-500 mb-2">누적 미정산액</p>
                    <p className="text-2xl font-bold text-gray-500"></p>
                </div>
                <div className="border border-gray-500 p-5 bg-white">
                    <p className="text-sm text-gray-500 mb-2">결제 실패/취소</p>
                    <p className="text-2xl font-bold text-gray-500"></p>
                </div>
            </div>
        </div>
    )
}