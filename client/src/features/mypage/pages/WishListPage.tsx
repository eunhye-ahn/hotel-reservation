import { ErrorMessage } from "@/common/component/ErrorMessage";
import { Spinner } from "@/common/component/Spinner";
import { useNavigate, useParams } from "react-router"
import { useWishCollectionDetail } from "../hooks/useWishCollectionDetail";
import { useDeleteCollection } from "../hooks/useDeleteCollection";
import { PrevBtn } from "@/common/component/PrevBtn"

export function WishListPage() {
    const { collectionId } = useParams();
    const navigate = useNavigate();

    const { data, isLoading, isError } = useWishCollectionDetail({
        collectionId: Number(collectionId)
    })

    const { delteCollectionMutate } = useDeleteCollection(Number(collectionId))

    const handleDelete = () => {
        delteCollectionMutate()
    }

    if (isLoading) return <Spinner />
    if (isError) return <ErrorMessage />

    return (
        <div className="detail-container">
            <PrevBtn />
            <div className="flex items-center justify-between mt-8 mb-6">
                <h2 className="text-xl font-bold mt-8 mb-6">{data?.name}</h2>
                <button
                    className="bg-gray-100 px-2 py-1 border border-gray-300 rounded-sm hover:bg-gray-300"
                    onClick={handleDelete}>삭제</button>
            </div>
            <div className="wishlist-detail-container">
                <div className="grid grid-cols-4 gap-4">
                    {data?.items.map((item, index) => (
                        <div
                            className=" overflow-hidden border border-gray-200 cursor-pointer hover:border-gray-300 transition-colors"
                            key={index}
                            onClick={() => navigate(`/hotels/${item.hotelId}`)}
                        >
                            <img
                                className="w-full object-cover"
                                src={item.hotelImageUrl}
                            />
                            <div className="p-3">
                                <p className="text-sm font-medium truncate">{item.hotelName}</p>
                                <p className="text-xs text-gray-400 truncate">{item.hotelAddress}</p>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    )
}