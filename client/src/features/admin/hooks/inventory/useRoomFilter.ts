import { format } from "date-fns"
import { useState } from "react"

export const useRoomFilter = () => {
    const [roomTypeId, setRoomTypeId] = useState<number | undefined>(undefined)
    const [floor, setFloor] = useState<number|undefined>(undefined)
    const [page, setPage] = useState<number>(0)
    const [targetDate, setTargetDate] = useState<string>(format(new Date(), "yyyy-MM-hh"))

    const filter = {roomTypeId, floor, targetDate, page}

    return {filter, setRoomTypeId, setFloor, setPage, setTargetDate}
}