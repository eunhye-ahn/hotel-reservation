import { ErrorMessage } from "@/common/component/ErrorMessage";
import { Spinner } from "@/common/component/Spinner";
import { useNavigate, useParams } from "react-router"
import { useWishCollectionDetail } from "../hooks/useWishCollectionDetail";

export function WishListPage() {
    const { collectionId } = useParams();
    const navigate = useNavigate();

    const { data, isLoading, isError } = useWishCollectionDetail({
        collectionId: Number(collectionId)
    })


    if (isLoading) return <Spinner />
    if (isError) return <ErrorMessage />

    return (
        <div>
            <h2>{data?.name}</h2>
            <div className="wishlist-detail-container">
                <div className="wishlist-detail-grid">
                    {data?.items.map((item, index) => (
                        <div className="wishlist-detail-card" key={index} onClick={() => navigate(`/hotels/${item.wishListItemId}`)}>
                            <img className="wishlist-detail-image" src={item.hotelImageUrl} />
                            <div className="wishlist-detail-info">
                                <p>{item.hotelName}</p>
                            </div>
                        </div>
                    ))}
                </div>
            </div>
        </div>
    )
}