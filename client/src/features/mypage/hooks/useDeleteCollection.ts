import { deleteCollection } from "@/api/api"
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { useNavigate } from "react-router"
import { wishCollectionKeys } from "./wishCollectionKeys"

export const useDeleteCollection = (collectionId: number) => {
    const navigate = useNavigate()
    const queryClient = useQueryClient()

    const { mutate: delteCollectionMutate } = useMutation({
        mutationFn: () => deleteCollection(collectionId),
        onSuccess: (() => {
            queryClient.invalidateQueries({ queryKey: wishCollectionKeys.list() })
            navigate("/mypage")
        })
    })

    return { delteCollectionMutate }
}