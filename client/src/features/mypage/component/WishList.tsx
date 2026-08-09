import { useState } from "react"
import { useNavigate } from "react-router"
import { Modal } from "../../../common/component/Modal"
import { useWishCollections } from "@/features/mypage/hooks/useWishCollections"
import { Spinner } from "@/common/component/Spinner"
import { ErrorMessage } from "@/common/component/ErrorMessage"
import { useCreateWishCollection } from "../hooks/useCreateWishCollection"
import Cookies from 'js-cookie'

export const WishList = () => {
    const [isWishOpen, setIsWishOpen] = useState<boolean>(false)
    const navigate = useNavigate()
    const [collectionName, setCollectionName] = useState<string>("")

    const { wishes, isLoading, isError, handleLoadMore, hasMore, handleCollapse, isExpanded } = useWishCollections()

    const { mutate, isPending, errorMsg, clearError } = useCreateWishCollection(() => {
        setIsWishOpen(false)
        setCollectionName("")
    })

    const handleAddCollection = () => {
        mutate({ collectionName: collectionName })
    }

    const handleSelectCollection = (collectionId: number) => {
        console.log(collectionId)
        console.log('저장 전:', Cookies.get('wish-collection'))
        Cookies.set('wish-collection', String(collectionId), { expires: 7 })
        console.log('저장 후:', Cookies.get('wish-collection'))
        navigate(`/wishlists/${collectionId}`)
    }

    if (isLoading) return <Spinner />
    if (isError) return <ErrorMessage />

    return (
        <div className="page-container">
            <div className="flex items-center mb-5 gap-5 justify-between">
                <h2 className="text-lg font-bold">위시 리스트</h2>
                <div className="flex items-center gap-3">
                    <button
                        className="w-8 h-8 rounded-full border border-gray-300 flex items-center justify-center cursoer-pointer"
                        onClick={() => {
                            clearError()
                            setIsWishOpen(true)
                        }}>+</button>
                    {hasMore && (
                        <button onClick={handleLoadMore}>
                            더보기
                        </button>
                    )}
                    {isExpanded && !hasMore && (
                        <button
                            onClick={handleCollapse}                >
                            접기
                        </button>
                    )}
                </div>
            </div>
            {isWishOpen &&
                <Modal isOpen={isWishOpen} onClose={() => setIsWishOpen(false)} title="위시리스트 만들기">
                    <input
                        className="w-full border border-gray-300 rounded-lg px-3 py-2 text-sm"
                        type="text" placeholder="이름"
                        value={collectionName}
                        onChange={(e) => {
                            clearError()
                            setCollectionName(e.target.value)
                        }}
                    />
                    {errorMsg && <p className="text-red-500 text-xxs mt-1">{errorMsg}</p>}
                    <div className="flex items-center gap-2 justify-end">
                        <button
                            className="bg-gray-100 py-2 px-4 rounded-lg cursor-pointer hover:bg-gray-200"
                            onClick={handleAddCollection} disabled={isPending}>생성</button>
                    </div>
                </Modal>
            }
            <div className="grid grid-cols-5 gap-4">
                {wishes?.length === 0 ? "위시리스트가 없습니다"
                    : wishes?.map((collection) => (
                        <div className="cursor-pointer" key={collection.collectionId} onClick={() => handleSelectCollection(collection.collectionId)}>
                            <div className="grid grid-cols-2 grid-rows-2 aspect-square gap-0.5 objecr-cover">
                                {collection.items.slice(0, 4).map((item, index) => (
                                    <img key={index} src={item.hotelImageUrl} alt={item.hotelName} className="w-full h-full object-cover" />
                                ))}
                            </div>
                            <div className="mt-2">
                                <p className="font-semibold text-center">{collection.name}</p>
                                <p className="text-xs text-center text-gray-500">저장된 항목 {collection.items.length}개</p>
                            </div>
                        </div>
                    ))}
            </div>
        </div >
    )
}