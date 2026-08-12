import { addWishList, cancelWishList, getWishedChecked } from "@/api/api";
import { useWishModalStore } from "@/store/useWishModalStore";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";
import { hotelKeys } from "./hotelkeys";
import { getErrorCode, getErrorMessage } from "@/api/errorHelpers";
import { toast } from "react-toastify";
import { wishCollectionKeys } from "@/features/mypage/hooks/wishCollectionKeys";
import { useAuthStore } from "@/store/useAuthStore";

//위시 조회 + 추가/취소 mutation
export const useWishList = (hotelId?: number) => {
    const { accessToken } = useAuthStore()
    const { open } = useWishModalStore()

    //상태
    const queryClient = useQueryClient();
    const [isWished, setIsWished] = useState<boolean>(false);

    const isValidId = typeof hotelId === 'number' && !Number.isNaN(hotelId);

    //위시여부확인
    const { data: hotelWishCheck } = useQuery<boolean>({
        queryKey: hotelKeys.wish(hotelId!),
        queryFn: () => getWishedChecked(Number(hotelId)).then((res) => res.data),
        enabled: isValidId && !!accessToken
    })

    //mutation 값 오면 상태변경 useEffect
    useEffect(() => {
        if (hotelWishCheck != undefined) {
            setIsWished(hotelWishCheck)
        }
    }, [hotelWishCheck])

    //저장 mutation
    const { mutate: addWishMutation } = useMutation({
        mutationFn: addWishList,
        onSuccess: () => {
            setIsWished(true)
            queryClient.invalidateQueries({ queryKey: wishCollectionKeys.list() })
            queryClient.invalidateQueries({ queryKey: hotelKeys.wish(hotelId!) })
        },
        onError: (err, variables) => {
            const code = getErrorCode(err)
            if (code === "COLLECTION_SELECT_REQUIRED") {
                open(variables)
                return
            }
            if (code === "WISHLIST_NOT_FOUND" || code === "COLLECTION_NOT_FOUND") {
                queryClient.invalidateQueries({ queryKey: wishCollectionKeys.list() })
                queryClient.invalidateQueries({ queryKey: hotelKeys.wish(hotelId!) })
                toast.error(getErrorMessage(err))
                return
            }
        }
    })

    //삭제 mutation
    const { mutate: cancelWishMutation } = useMutation({
        mutationFn: cancelWishList,
        onSuccess: () => {
            setIsWished(false)
            queryClient.invalidateQueries({ queryKey: wishCollectionKeys.list() })
            queryClient.invalidateQueries({ queryKey: hotelKeys.wish(hotelId!) })
        },
        onError: (err) => {
            const code = getErrorCode(err)

            if (code === "WISHLIST_NOT_FOUND") {
                queryClient.invalidateQueries({ queryKey: hotelKeys.wish(hotelId!) })
                queryClient.invalidateQueries({ queryKey: wishCollectionKeys.list() })
            }
            toast.error(getErrorMessage(err))
        }
    })

    //handle
    const handleWish = (hotelId: number | undefined) => {
        if (!hotelId) return
        isWished ? cancelWishMutation(hotelId) : addWishMutation(hotelId)
    }

    return { isWished, handleWish }
}