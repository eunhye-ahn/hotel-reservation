import type { AdminPaymentSearchRequest } from "@/type/admin";
import { useState } from "react";

export const usePaymentFilter = () => {

    const [searchType, setSearchType] = useState<string | undefined>("USER_NAME")
    const [keyword, setKeyword] = useState<string | undefined>(undefined)
    const [startDate, setStartDate] = useState<string | undefined>(undefined)
    const [endDate, setEndDate] = useState<string | undefined>(undefined)
    const [status, setStatus] = useState<string | undefined>(undefined)
    const [page, setPage] = useState<number>(0)

    const filter: AdminPaymentSearchRequest = {
        searchType,
        keyword,
        startDate,
        endDate,
        status,
        page
    };

    const reset = () => {
        setSearchType(undefined);
        setKeyword("");
        setStartDate(undefined);
        setEndDate(undefined);
        setStatus(undefined);
        setPage(0);
    };

    return { filter, setSearchType, setKeyword, setStartDate, setEndDate, setPage, setStatus }
}