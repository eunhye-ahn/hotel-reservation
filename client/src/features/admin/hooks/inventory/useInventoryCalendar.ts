import { getInventoryCalendar } from "@/api/api"
import { useQuery } from "@tanstack/react-query"
import { adminInventoryKeys } from "./adminInventorykey"


export const useInventoryCalendar = (hotelId: number, startDate: string, endDate: string) => {
    const { data: calendarData, isLoading: isCalendarLoading, isError: isCalendarError } = useQuery({
        queryKey: [adminInventoryKeys.calendar(hotelId, startDate, endDate)],
        queryFn: () => getInventoryCalendar(hotelId, startDate, endDate).then(res => res.data)
    })

    return { calendarData, isCalendarLoading, isCalendarError }
}