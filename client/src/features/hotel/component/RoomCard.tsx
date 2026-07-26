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
        <div className="room-card" key={roomTypeId}>
            <img src={imageUrl} />
            <div className="room-card-info">
                <p>{name}</p>
                <p>숙박 {checkInTime.substring(0, 5)}~{checkOutTime.substring(0, 5)}</p>
                <p>남은객실 {availableCount}개</p>
            </div>
            <div className="room-card-price">
                <div className="hotel-price-row">
                    <span className="hotel-original">{maxRate.toLocaleString()}</span>
                    <span className="hotel-discount">{discountRate}%</span>
                </div>
                <p className="hotel-demand">{demandRate.toLocaleString()}원</p>
                <button onClick={() => onReserve(roomTypeId)} disabled={isPending}>
                    {isPending ? "Loading..." : "예약하기"}
                </button>
            </div>
        </div>
    )
}