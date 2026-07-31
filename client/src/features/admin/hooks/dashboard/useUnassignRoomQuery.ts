import { getUnAssignReservationInfo } from "@/api/api"
import { useQuery } from "@tanstack/react-query"
import { adminDashboardKeys } from "./adminDashboardKeys"

export const useUnassignRoomQuery = () => {
    const { data: unassignRoomData, isLoading: isUnassignRoomLoading, isError: isUnassignRoomError } = useQuery({
        queryKey: adminDashboardKeys.unAssignRoom,
        queryFn: () => getUnAssignReservationInfo().then(res => res.data)
    })

    return { unassignRoomData, isUnassignRoomError, isUnassignRoomLoading }
}