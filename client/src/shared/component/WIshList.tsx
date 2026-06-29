import { getCollection, getCollections } from "@/api/reservation-service"
import { useQuery, useQueryClient } from "@tanstack/react-query"
import type { WishListCollectionResponse } from "../type/hotel"
import { useState } from "react"
import { useNavigate } from "react-router"
import '@/shared/component/WishList.css'

export const WishList = () => {
    const [selectedId, setSelectedId] = useState<number|null>(null);
    const navigate = useNavigate();

    const queryClient = useQueryClient();

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
        <div className="wishlist-container">
            <h2>위시 리스트</h2>
            <div className="wishlist-grid">
                {data?.map((colleciton)=>(
                    <div className="wishlist-card" key={colleciton.collectionId} onClick={()=>navigate(`/wishlists/${colleciton.collectionId}`)}>
                        <div className="wishlist-image-grid">
                            {colleciton.items.slice(0,4).map((item,index)=>(
                                <img key={index} src={item.hotelImageUrl} alt={item.hotelName}/>
                            ))}
                        </div>
                        <div className="wishlist-card-info">
                            <p className="collection-name">{colleciton.name}</p>
                            <p className="collection-count">저장된 항목 {colleciton.count}개</p>
                        </div>
                    </div>
                ))}
            </div>
        </div>
    )
}