import { useState } from "react"

export const useSettlementHistoryFilter = () => {
    const [startDate, setStartDate] = useState<string | undefined>(undefined)
    const [status, setStatus] = useState<string | undefined>(undefined)
    const [endDate, setEndDate] = useState<string | undefined>(undefined)
    const [page, setPage] = useState<number>(0)

    const filter = { startDate, endDate, status, page }

    return { filter, setStartDate, setStatus, setEndDate, setPage }
}