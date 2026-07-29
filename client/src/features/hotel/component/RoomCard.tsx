import { Spinner } from "@/common/component/Spinner"

interface RoomCardProps {
    roomTypeId: number,
    imageUrl: string,
    name: string,
    checkInTime: string,
    checkOutTime: string,
    availableCount: number,
    maxRate: number,
    discountRate: number,
    demandRate: number,
    isPending: boolean,
    onReserve: (roomTypeId: number) => void
}

export const RoomCard = ({
    roomTypeId,
    imageUrl,
    name,
    checkInTime,
    checkOutTime,
    availableCount,
    maxRate,
    discountRate,
    demandRate,
    isPending,
    onReserve
}: RoomCardProps) => {
    return (
        <div className="flex gap-4 py-4" key={roomTypeId}>
            <img className="w-[180px] h-[130px] object-cover"
                src={imageUrl} />
            <div className="flex-1">
                <p className="font-semibold">{name}</p>
                <p className="text-xs text-gray-500 mt-1">숙박 {checkInTime.substring(0, 5)}~{checkOutTime.substring(0, 5)}</p>
                <p className="text-xs text-gray-500">남은객실 {availableCount}개</p>
            </div>
            <div className="flex flex-col justify-between">
                <div className="text-right">
                    <div className="flex items-center gap-1 ">
                        <span className="text-xs text-gray-400 line-through">{maxRate.toLocaleString()}</span>
                        <span className="text-red-500 font-bold text-sm">{discountRate}%</span>
                    </div>

                    <p className="font-bold text-lg mt-1">{demandRate.toLocaleString()}원</p>
                </div>
                <button
                    className="bg-gray-100 rounded-md px-4 py-1 text-sm hover:bg-gray-200 disabled:opacity-40 disabled:cursor-not-allowed cursor-pointer"
                    onClick={() => onReserve(roomTypeId)} disabled={isPending}>
                    {isPending ? <Spinner /> : "예약하기"}
                </button>
            </div>
        </div>
    )
}