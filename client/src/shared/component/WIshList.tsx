import { getCollection, getCollections } from "@/api/reservation-service"
import { useQuery } from "@tanstack/react-query"
import type { WishListCollectionResponse } from "../type/hotel"
import { useState } from "react"
import { useNavigate } from "react-router"

export const WishList = () => {
    const [selectedId, setSelectedId] = useState<number|null>(null);
    const navigate = useNavigate();

    const {data, isLoading, isError} = useQuery<WishListCollectionResponse[]>({
        queryKey: ["wishs"],
        queryFn: () => getCollections().then(res=>{
            console.log(res.data)
            return res.data
        })
    })

    if (isLoading) return <div>로딩 중...</div>
    if (isError) return <div>에러가 발생했습니다</div>

    return (
        <div>
            <h2>위시 리스트</h2>
            {data?.map((colleciton)=>(
                <div key={colleciton.collectionId} onClick={()=>navigate(`/wishlists/${colleciton.collectionId}`)}>
                    <div>
                        {colleciton.items.slice(0,4).map((item,index)=>(
                            <img key={index} src={item.hotelImageUrl} alt={item.hotelName}/>
                        ))}
                    </div>
                    <p>{colleciton.name}</p>
                </div>
            ))}
        </div>
    )
}