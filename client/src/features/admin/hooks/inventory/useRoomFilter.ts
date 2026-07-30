import { useState } from "react"

export const useRoomFilter = () => {
    const [roomTypeId, setRoomTypeId] = useState<number | undefined>(undefined)
    const [floor, setFloor] = useState<number|undefined>(undefined)
    const [page, setPage] = useState<number>(0)

    const filter = {roomTypeId, floor, page}

    return {filter, setRoomTypeId, setFloor, setPage}
}