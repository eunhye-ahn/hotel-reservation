import { useState } from "react"
import { addDays, format, subDays } from "date-fns"
import { useInventoryCalendar } from "../../hooks/inventory/useInventoryCalendar"
import { Spinner } from "@/common/component/Spinner"
import { ErrorMessage } from "@/common/component/ErrorMessage"

export const InventoryCalendar = ({ hotelId }: { hotelId: number }) => {

    const [weekStart, setWeekStart] = useState(subDays(new Date(), 7))
    const startDate = format(weekStart, 'yyyy-MM-dd')
    const endDate = format(addDays(weekStart, 6), 'yyyy-MM-dd')

    const { calendarData, isCalendarLoading, isCalendarError } = useInventoryCalendar(Number(hotelId), startDate, endDate)

    const dates = Array.from({ length: 7 }, (_, i) => addDays(weekStart, i))

    const getCellStyle = (available: number) => {
        if (available < 0) return "bg-red-50 text-red-600 border-red-200"
        if (available <= 1) return "bg-orange-50 text-orange-600 border-orange-200"
        return "bg-green-50 text-green-700 border-green-200"
    }

    if(isCalendarLoading) return <Spinner/>
    if(isCalendarError) return <ErrorMessage/>

    return (
        <div>
            <div className="flex items-center justify-between px-5 py-3 bg-gray-50 border-b border-gray-200">
                <span className="font-semibold text-sm">객실타입별 재고 캘린더</span>
                <div className="flex gap-2 text-sm items-center">
                    <button
                        className="px-2 py-1 border border-gray-500 text-sm bg-gray-100 cursor-pointer hover:bg-gray-200"
                        onClick={() => setWeekStart(prev => subDays(prev, 7))}
                    >
                        이전주
                    </button>
                    {startDate} ~ {endDate}
                    <button
                        className="px-2 py-1 text-sm border border-gray-500 bg-gray-100 cursor-pointer hover:bg-gray-200"
                        onClick={() => setWeekStart(prev => addDays(prev, 7))}
                    >
                        다음주
                    </button>
                </div>
            </div>
            <div className="flex items-center gap-4 px-5 py-3 text-xs">
                <span className="flex items-center gap-1">
                    <span className="w-2 h-2  bg-green-500"></span> 여유
                </span>
                <span className="flex items-center gap-1">
                    <span className="w-2 h-2 bg-orange-500"></span> 임박
                </span>
                <span className="flex items-center gap-1">
                    <span className="w-2 h-2 bg-red-500"></span> 초과예약(110%↑)
                </span>
            </div>
            <table className="w-full text-sm">
                <thead>
                    <tr>
                        <th>객실타입</th>
                        {dates.map(d => (
                            <th key={d.toISOString()} >
                                {format(d, 'M/d')}
                            </th>
                        ))}
                    </tr>
                </thead>
                <tbody>
                    {calendarData?.map(roomType => (
                        <tr key={roomType.roomTypeId}>
                            <td className="px-4 py-5 font-medium text-center w-[10%]">
                                {roomType.roomTypeName}
                            </td>
                            {dates.map(d => {
                                const dateStr = format(d, 'yyyy-MM-dd')
                                const cell = roomType.cells.find(c => c.date == dateStr)
                                if (!cell) {
                                    return (
                                        <td key={dateStr} className="px-2 py-2">
                                            <div>-</div>
                                        </td>
                                    )
                                }
                                const available = cell.totalInventory - cell.totalReserved

                                return (
                                    <td key={dateStr} className="px-2 py-2">
                                        <button
                                            className={`w-full border py-4 flex gap-1 justify-center  items-baseline  text-center cursor-pointer ${getCellStyle(available)}`}
                                        >
                                            <div className="font-bold">{available}</div>
                                            <div className="text-xs text-gray-500">/{cell.totalInventory}</div>
                                        </button>
                                    </td>
                                )
                            })}
                        </tr>
                    ))}
                </tbody>
            </table>
        </div>
    )
}