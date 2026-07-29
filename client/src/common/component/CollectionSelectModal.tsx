import { useWishCollections } from "@/features/mypage/hooks/useWishCollections"
import { useWishModalStore } from "@/store/useWishModalStore"
import { Modal } from "./Modal"
import Cookies from 'js-cookie'
import { useWishList } from "@/features/hotel/hooks/useWishList"
import { useNavigate } from "react-router"

export const CollectionSelectModal = () => {
    const { isOpen, hotelId, close } = useWishModalStore()
    const { data, isLoading } = useWishCollections(isOpen)
    const { handleWish } = useWishList()
    //const {navigate} = useNavigate()


    const handleSelect = (collectionId: number) => {
        Cookies.set('wish-collection', String(collectionId), { expires: 7 })
        handleWish(Number(hotelId))
        close()
        //위시여부 확인 로직 재생성 필요 => 페이지이동을 한다던가
        //navigate("")
    }

    if (!isOpen) return null

    return (
        <Modal isOpen={isOpen} onClose={close} title="콜렉션 선택">
            {isLoading ? (
                <div>로딩 중...</div>
            ) : (
                <ul className="collection-select-list">
                    {data?.map((collection) => (
                        <li key={collection.collectionId} onClick={() => handleSelect(collection.collectionId)}>
                            {collection.name}
                        </li>
                    ))}
                </ul>
            )}
        </Modal>
    )
}