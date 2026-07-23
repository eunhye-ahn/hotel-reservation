import { useQuery } from "@tanstack/react-query"
import { getReservations } from "@/api/api";
import type { AdminReseervationSearchRequest } from "@/type/admin";

export const useReservations = (filter: AdminReseervationSearchRequest) => {

    const { data, isLoading, isError } = useQuery({
        queryKey: ["reservations", filter],
        queryFn: () => getReservations(filter).then((res) => res.data)
    })

    return { data, isLoading, isError }
}