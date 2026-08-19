import { useParams } from "react-router"
import { AdminRoomList } from "../component/inventory/AdminRoomList"
import { InventoryCalendar } from "../component/inventory/InventoryCalendar"
import { useState } from "react"
import { format } from "date-fns"


export const InventoryDetail = () => {
    const { hotelId } = useParams<string>()
    const [inventorySelected, setInventorySelected] = useState<{roomTypeId: number|null; dateStr: string}>({
        roomTypeId: null,
        dateStr: format(new Date(), "yyyy-MM-hh")
    })

    return (
        <>
            <InventoryCalendar 
                hotelId={Number(hotelId)} 
                onSelected={(value)=>setInventorySelected(value)}
            />
            <AdminRoomList 
                hotelId={Number(hotelId)} 
                inventorySelected={inventorySelected}
            />
        </>
    )
}