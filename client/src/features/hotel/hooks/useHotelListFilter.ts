import { addDays, format } from "date-fns";
import { useSearchParams } from "react-router"

export const useHotelListFilter = () => {
    const [searchParams] = useSearchParams()
    const today = format(new Date, 'yyyy-MM-dd')
    const tomorrow = format(addDays(new Date, 1), 'yyyy-MM-dd')

    const filter = {
        regionCode: searchParams.get("lDongRegnCd") ?? "",
        subRegionCode: searchParams.get("lDongSignguCd") ?? "",
        q: searchParams.get("q") ?? "",
        checkIn: searchParams.get("startDate") ?? today,
        checkOut: searchParams.get("endDate") ?? tomorrow,
        lclsSystm2: searchParams.get("lclsSystm2"),
        numberOfGuests: Number(searchParams.get("numberOfGuests") ?? 3),
        numberOfRooms: Number(searchParams.get("roomToReserve") ?? 1),
    }

    return { filter }
}