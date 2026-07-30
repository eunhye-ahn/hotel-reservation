import { previewSettlementAmount } from "@/api/api"
import { useQuery } from "@tanstack/react-query"
import { adminSettlementKeys } from "./adminSettlementKeys"

interface useSettlementPreviewProps {
    hotelId: number,
    periodStart: string | undefined,
    periodEnd: string | undefined
}

export const useSettlementPreview = ({ hotelId, periodStart, periodEnd }: useSettlementPreviewProps) => {
    const { data, isLoading, isError } = useQuery({
        queryKey: adminSettlementKeys.preview(hotelId, periodStart, periodEnd),
        queryFn: () => previewSettlementAmount(hotelId, periodStart, periodEnd)
            .then(res => res.data),

    })

    return { data, isLoading, isError }
}