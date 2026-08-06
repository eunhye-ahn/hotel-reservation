import { getCollections } from "@/api/api"
import { useQuery } from "@tanstack/react-query"
import { wishCollectionKeys } from "./wishCollectionKeys"
import { useState } from "react"

export const useWishCollections = (enabled: boolean = true) => {
    const INITIAL_COUNT = 5
    const [visibleCount, setVisibleCount] = useState(INITIAL_COUNT)

    const { data, isLoading, isError } = useQuery({
        queryKey: wishCollectionKeys.list(),
        queryFn: () => getCollections().then(res => {
            return res.data
        }),
        enabled
    })

    const wishes = data?.slice(0, visibleCount)
    const hasMore = data ? visibleCount < data.length : false
    const isExpanded = visibleCount > INITIAL_COUNT

    const handleLoadMore = () => {
        setVisibleCount(prev => prev + 5)
    }
    const handleCollapse = () => {
        setVisibleCount(INITIAL_COUNT)
    }

    return { wishes, isLoading, isError, handleLoadMore, hasMore, handleCollapse, isExpanded }
}