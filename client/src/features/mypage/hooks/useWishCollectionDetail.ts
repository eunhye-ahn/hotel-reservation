import { useQuery } from "@tanstack/react-query"
import { wishCollectionKeys } from "./wishCollectionKeys"
import { getCollection } from "@/api/api"

export const useWishCollectionDetail = ({ collectionId }: { collectionId: number }) => {
    const { data, isLoading, isError } = useQuery({
        queryKey: wishCollectionKeys.detail(collectionId),
        queryFn: () => getCollection(collectionId).then(res => {
            console.log(res.data)
            return res.data
        })
    })

    return { data, isLoading, isError }
}