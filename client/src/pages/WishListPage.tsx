import { getCollection } from "@/api/reservation-service";
import { SearchFilterBar } from "@/shared/component/SearchFilterBar";
import { useQuery } from "@tanstack/react-query";
import { useState } from "react";
import { useParams, useSearchParams } from "react-router"

export function WishListPage(){
    const {collectionId} = useParams();
    const [searchParams] = useSearchParams();
    const [isDateOpen, setIsDateOpen] = useState(false);
    const [isFilterOpen, setIsFilterOpen] = useState(false);
    const [isSortOpen, setIsSortOpen] = useState(false);

    const today = new Date().toLocaleDateString('en-CA')
    const tomorrow = new Date(Date.now() + 86400000).toLocaleDateString('en-CA');



    const checkIn = searchParams.get("startDate") ?? today;
    const checkOut = searchParams.get("endDate") ?? tomorrow;
    const lclsSystm2 = searchParams.get("lclsSystm2");
    const numberOfGuests = Number(searchParams.get("numberOfGuests") ?? 3);
    const numberOfRooms = Number(searchParams.get("roomToReserve") ?? 1);


    const {data,isLoading, isError} = useQuery({
        queryKey:[],
        queryFn:()=>getCollection(Number(collectionId)).then(res=>{
            console.log(res.data)
            return res.data
        })
    })

    if (isLoading) return <div>로딩 중...</div>
    if (isError) return <div>에러가 발생했습니다</div>

    return(
        <div>
            <SearchFilterBar
                            checkIn={checkIn}
                            checkOut={checkOut}
                            guestToReserve={numberOfGuests}
                            roomToReserve={numberOfRooms}
                            onDateClick={() => setIsDateOpen(true)}
                            onFilterClick={() => setIsFilterOpen(true)}
                            onSortClick={() => setIsSortOpen(true)}
                        />
            <h2>{data?.name}</h2>
            <div>
            {data?.items.map(item=>(
                <div>
                    <img src={item.hotelImageUrl}/>
                    <p>{item.hotelName}</p>
                </div>
            ))}
            </div>
        </div>
    )
}