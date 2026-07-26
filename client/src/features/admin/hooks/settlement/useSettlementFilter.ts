import { useState } from "react"

export const useSettlementFilter = () => {
    const [searchType, setSearchType] = useState<string | undefined>("HOTEL_NAME")
    const [keyword, setKeyword] = useState<string | undefined>(undefined)
    const [hasPendingBalance, setHasPendingBalance] = useState<boolean | undefined>(undefined)
    const [sortType, setSortType] = useState<string | undefined>("BALANCE")
    const [page, setPage] = useState<number>(0)

    const filter = { searchType, keyword, hasPendingBalance, sortType, page }

    return { filter, setSearchType, setKeyword, setHasPendingBalance, setSortType, setPage }
}