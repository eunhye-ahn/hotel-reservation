import { addWishList, cancelWishList, getWishedChecked } from "@/api/api";
import { useWishModalStore } from "@/store/useWishModalStore";
import { useMutation, useQuery, useQueryClient } from "@tanstack/react-query";
import { useEffect, useState } from "react";

//위시 조회 + 추가/취소 mutation
export const useWishList = (hotelId?: number) => {

    const { open } = useWishModalStore()

    //상태
    const queryClient = useQueryClient();
    const [isWished, setIsWished] = useState<boolean>(false);

    const isValidId = typeof hotelId === 'number' && !Number.isNaN(hotelId);

    //위시여부확인 query => 호텔아이디 props
    const { data: hotelWishCheck } = useQuery<boolean>({
        queryKey: ["wishCheck", hotelId],
        queryFn: () => getWishedChecked(Number(hotelId)).then((res) => res.data),
        enabled: isValidId
    })

    //mutation 값 오면 상태변경 useEffect
    useEffect(() => {
        if (hotelWishCheck != undefined) {
            setIsWished(hotelWishCheck)
            console.log(hotelWishCheck)
        }
    }, [hotelWishCheck])

    //저장 mutation
    const { mutate: addWishMutation } = useMutation({
        mutationFn: addWishList,
        onSuccess: (res) => {
            setIsWished(true)
            queryClient.invalidateQueries({ queryKey: ["wishList", "wishs"] })
        },
        onError: (err: any, variables) => {
            console.log(err.response.data.code)
            if (err.response.data.code === "COLLECTION_SELECT_REQUIRED") {
                open(variables)
            }
            console.log(err)
        }
    })

    //삭제 mutation
    const { mutate: cancelWishMutation } = useMutation({
        mutationFn: cancelWishList,
        onSuccess: (res) => {
            setIsWished(false)
            queryClient.invalidateQueries({ queryKey: ["wishList"] })
        },
        onError: (err: any) => {
            console.log(err)
        }
    })

    //handle
    const handleWish = (hotelId: number | undefined) => {
        if (!hotelId) return
        isWished ? cancelWishMutation(hotelId) : addWishMutation(hotelId)
    }

    return { isWished, handleWish }
}