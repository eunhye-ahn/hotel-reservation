import type { AdminReseervationSearchRequest } from "@/api/types/admin";
import { useState } from "react";

export const useReservationsFilter = () => {
    const [searchType, setSearchType] = useState<string | undefined>("USER_NAME");
    const [keyword, setKeyword] = useState("");
    const [startDate, setStartDate] = useState<string | undefined>(undefined);
    const [endDate, setEndDate] = useState<string | undefined>(undefined);
    const [status, setStatus] = useState<string | undefined>(undefined);
    const [roomAssigned, setRoomAssigned] = useState<boolean | undefined>(undefined);
    const [page, setPage] = useState(0);

    const filter: AdminReseervationSearchRequest = {
        searchType,
        keyword,
        startDate,
        endDate,
        status,
        roomAssigned,
        page,
    };

    const reset = () => {
        setSearchType(undefined);
        setKeyword("");
        setStartDate(undefined);
        setEndDate(undefined);
        setStatus(undefined);
        setRoomAssigned(undefined);
        setPage(0);
    };

    return { filter, setSearchType, setKeyword, setStartDate, setEndDate, setPage, setRoomAssigned, setStatus, reset }
}