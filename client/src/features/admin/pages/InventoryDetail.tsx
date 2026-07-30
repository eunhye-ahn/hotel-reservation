import { useParams } from "react-router"
import { AdminRoomList } from "../component/inventory/AdminRoomList"
import { InventoryCalendar } from "../component/inventory/InventoryCalendar"


export const InventoryDetail = () => {
    const { hotelId } = useParams<string>()

    return (
        <>
            <InventoryCalendar hotelId={Number(hotelId)} />
            <AdminRoomList hotelId={Number(hotelId)} />
        </>
    )
}