import { createCollection, getCollections } from "@/api/api"
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query"
import type {  WishCollectionsRequest, WishListCollectionResponse } from "../type/hotel"
import { useState } from "react"
import { useNavigate } from "react-router"
import '@/css/WishList.css'
import { toast } from "react-toastify"
import { Modal } from "./Modal"
import type { AxiosResponse } from "axios"
import { useWishCollections } from "@/hooks/useWishCollections"

//wishcollection으로 유즈스테이트타입을 잡았어야했나 왜 프로퍼티가 null일까

export const WishList = () => {
    const [isWishOpen, setIsWishOpen] = useState<boolean>(false);
    const navigate = useNavigate();
    const [collectionName, setCollectionName] = useState<string>("");

    const queryClient = useQueryClient();

    const {data, isLoading, isError} = useWishCollections();

    const {mutate, isPending} = useMutation<AxiosResponse<void>, any, WishCollectionsRequest>({
        mutationFn: createCollection,
        onSuccess: (()=>{
            queryClient.invalidateQueries({queryKey: ["wishs"]})
        }),
        onError:((err: any)=>{
            toast.error(err.response.data.message);
        })
    })

    const handleAddCollection = () => {
        mutate({collectionName: collectionName})
        setIsWishOpen(false)
    }

    if (isLoading) return <div>로딩 중...</div>
    if (isError) return <div>에러가 발생했습니다</div>

    return (
        <div className="wishlist-container">
            <h2>위시 리스트</h2>
            <button onClick={()=>setIsWishOpen(true)}>+</button>
            {isWishOpen &&
                <Modal isOpen={isWishOpen} onClose={()=>setIsWishOpen(false)} title="위시리스트 만들기">
                        <input type="text" placeholder="이름" onChange={(e)=>setCollectionName(e.target.value)}/>
                        <hr/>
                        <button onClick={()=>setIsWishOpen(false)}>취소</button>
                        <button onClick={handleAddCollection} disabled={isPending}>새로만들기</button>
                </Modal>
            }   
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