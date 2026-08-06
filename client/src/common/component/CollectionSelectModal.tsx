import { useWishCollections } from "@/features/mypage/hooks/useWishCollections"
import { useWishModalStore } from "@/store/useWishModalStore"
import { Modal } from "./Modal"
import Cookies from 'js-cookie'
import { Spinner } from "./Spinner"
import { ErrorMessage } from "./ErrorMessage"

export const CollectionSelectModal = () => {
    const { isOpen, close } = useWishModalStore()
    const { wishes, isLoading, isError } = useWishCollections(isOpen)


    const handleSelect = (collectionId: number) => {
        Cookies.set('wish-collection', String(collectionId), { expires: 7 })
        close()
    }

    return (
        <Modal isOpen={isOpen} onClose={close} title="콜렉션 선택">
            {isLoading ? (
                <Spinner />
            ) : isError ? <ErrorMessage />
                : (
                    <ul className="collection-select-list">
                        {wishes?.map((collection) => (
                            <li key={collection.collectionId} onClick={() => handleSelect(collection.collectionId)}>
                                {collection.name}
                            </li>
                        ))}
                    </ul>
                )
            }
        </Modal>
    )
}