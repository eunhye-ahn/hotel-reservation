import { getErrorCode, getErrorMessage } from "@/api/errorHelpers"
import { useMutation, useQueryClient } from "@tanstack/react-query"
import { wishCollectionKeys } from "./wishCollectionKeys"
import { useState } from "react"
import type { AxiosError, AxiosResponse } from "axios"
import type { WishCollectionsRequest } from "@/api/types/hotel"
import type { CustomErrorResponse } from "@/api/types/error"
import { toast } from "react-toastify"
import { createCollection } from "@/api/api"

export const useCreateWishCollection = (onSuccessCallback: () => void) => {
    const queryClient = useQueryClient()
    const [errorMsg, setErrorMsg] = useState<string>("")

    const { mutate, isPending } = useMutation<AxiosResponse<void>, AxiosError<CustomErrorResponse>, WishCollectionsRequest>({
        mutationFn: createCollection,
        onSuccess: (() => {
            queryClient.invalidateQueries({ queryKey: wishCollectionKeys.list() })
            onSuccessCallback()
            setErrorMsg("")
        }),
        onError: ((err) => {
            if (getErrorCode(err) === "COLLECTION_ALREADY_EXISTS") {
                setErrorMsg(getErrorMessage(err))
                return
            }
            toast.error(getErrorMessage(err));
        })
    })

    const clearError = () => setErrorMsg("")

    return { mutate, isPending, errorMsg, clearError }
}