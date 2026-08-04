import { getCollections } from "@/api/api"
import type { WishListCollectionResponse } from "@/api/types/hotel"
import { useQuery } from "@tanstack/react-query"
import { wishCollectionKeys } from "./wishCollectionKeys"

export const useWishCollections = (enabled: boolean = true) => {
    const { data, isLoading, isError } = useQuery<WishListCollectionResponse[]>({
        queryKey: wishCollectionKeys.list(),
        queryFn: () => getCollections().then(res => {
            return res.data
        }),
        enabled
    })

    return { data, isLoading, isError }
}