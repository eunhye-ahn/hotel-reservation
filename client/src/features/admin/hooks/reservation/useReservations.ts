import { useQuery } from "@tanstack/react-query"
import { getReservations } from "@/api/api";
import type { AdminReseervationSearchRequest } from "@/api/types/admin";
import { adminReservationKeys } from "./adminReservationKeys";

export const useReservations = (filter: AdminReseervationSearchRequest) => {

    const { data, isLoading, isError } = useQuery({
        queryKey: adminReservationKeys.list(filter),
        queryFn: () => getReservations(filter).then((res) => res.data)
    })

    return { data, isLoading, isError }
}