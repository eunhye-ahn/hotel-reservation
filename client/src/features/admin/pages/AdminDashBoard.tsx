import { DailyStatisticsChart } from "../component/dashboard/DailyStatisticsChart"
import { DashBoardSummary } from "../component/dashboard/DashBoardSummary"
import { PaymentStaticsChart } from "../component/dashboard/PaymentStaticsChart"
import { ReserveStatisticsChart } from "../component/dashboard/ReserveStaticsChart"
import { TopPendingHotelList } from "../component/dashboard/TopPendingHotelList"
import { UnassginRoomList } from "../component/dashboard/UnassignRoomList"

export const AdminDashBoard = () => {
    return (
        <>
            <DashBoardSummary />
            <div className="grid grid-cols-12 gap-4 mt-8">
                <div className="col-span-8">
                    <DailyStatisticsChart />
                </div>
                <div className="col-span-4">
                    <UnassginRoomList />
                </div>
            </div>
            <div className="grid grid-cols-12 gap-4 mt-8">
                <div className="col-span-4">
                    <ReserveStatisticsChart />
                </div>
                <div className="col-span-4">
                    <PaymentStaticsChart />
                </div>
                <div className="col-span-4">
                    <TopPendingHotelList />
                </div>
            </div>

        </>
    )
}