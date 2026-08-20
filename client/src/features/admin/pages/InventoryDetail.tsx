import { useLocation, useParams } from "react-router"
import { AdminRoomList } from "../component/inventory/AdminRoomList"
import { InventoryCalendar } from "../component/inventory/InventoryCalendar"
import { useState } from "react"
import { format } from "date-fns"


export const InventoryDetail = () => {
    const { hotelId } = useParams<string>()
    const location = useLocation()
    const hotelName = location.state?.hotelName
    const [inventorySelected, setInventorySelected] = useState<{
        roomTypeId: number|null, 
        dateStr: string,
        totalInventory: number|null,
        totalReserved: number|null
    }>({
        roomTypeId: null,
        dateStr: format(new Date(), "yyyy-MM-dd"),
        totalInventory: null,
        totalReserved: null
    })

    return (
        <>
            <p className="font-bold text-lg px-2 my-5">{hotelName} 재고</p>
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