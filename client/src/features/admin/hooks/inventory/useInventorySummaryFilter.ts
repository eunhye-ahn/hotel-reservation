import { format } from "date-fns"
import { useState } from "react"

export const useInventorySummaryFilter = () => {
    const [date, setDate] = useState<string>(format(new Date(), 'yyyy-MM-dd'))
    const [hotelName, setHotelName] = useState<string | undefined>(undefined)
    const [sortType, setSortType] = useState<string>("RESERVE_RATE_DESC")
    const [page, setPage] = useState<number>(0)

    const filter = { date, hotelName, sortType, page }

    return { filter, setDate, setHotelName, setSortType, setPage }
}