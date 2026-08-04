import { getMyInfo } from "@/api/api";
import { useQuery } from "@tanstack/react-query";
import { userKeys } from "./userKeys";

export const useMyInfo = (enabled: boolean) => {
    const { data: myInfo, isLoading: isMyInfoLoading, isError: isMyInfoError } = useQuery({
        queryKey: [userKeys.myInfo],
        queryFn: () => getMyInfo().then((res) => res.data),
        enabled
    });

    return { myInfo, isMyInfoLoading, isMyInfoError }
}