import { getHotelDetail } from "@/api/api";
import { useQuery } from "@tanstack/react-query";
import { hotelKeys } from "./hotelkeys";

export interface UseHotelDetailProps {
    hotelId: string | undefined,
    startDate: string,
    endDate: string,
    numberOfRooms: number,
    numberOfGuests: number
}

export const useHotelDetail = ({ hotelId, startDate, endDate, numberOfRooms, numberOfGuests }: UseHotelDetailProps) => {
    const { data, isLoading, isError, error } = useQuery({
        queryKey: hotelKeys.detail({ hotelId, startDate, endDate, numberOfGuests, numberOfRooms }),
        queryFn: () => getHotelDetail(Number(hotelId), startDate, endDate, numberOfRooms, numberOfGuests).then((res) => res.data)
    });

    return { data, isLoading, isError, error }
}