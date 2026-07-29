import { getCollections } from "@/api/api"
import type { WishListCollectionResponse } from "@/type/hotel"
import { useQuery } from "@tanstack/react-query"

export const useWishCollections = (enabled: boolean = true) => {
    const {data, isLoading, isError} = useQuery<WishListCollectionResponse[]>({
        queryKey: ["wishs"],
        queryFn: () => getCollections().then(res=>{
            console.log(res.data)
            return res.data
        }),
        enabled
    })

    return {data, isLoading, isError}
}