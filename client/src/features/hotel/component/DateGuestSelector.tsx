import { DayPicker } from "react-day-picker";
import type { DateRange } from "react-day-picker";
import "react-day-picker/style.css";
import { ko } from "date-fns/locale";
import { useState } from "react";
import { useSearchParams } from "react-router";
import { CalendarIcon } from "lucide-react";

interface DateGuestSelector {
    onClose: () => void
}

export const DateGuestSelector = ({ onClose }: DateGuestSelector) => {
    const today = new Date()
    const tomorrow = new Date(Date.now() + 86400000)
    const formatDate = (date: Date) => {
        const mm = String(date.getMonth() + 1).padStart(2, "0");
        const dd = String(date.getDate()).padStart(2, "0");
        const days = ["일", "월", "화", "수", "목", "금", "토"];
        return `${mm}.${dd}(${days[date.getDay()]})`;
    }

    const [searchParams, setSearchParams] = useSearchParams();
    const [numberOfGuests, setNumberOfGuests] = useState<number>(Number(searchParams.get("numberOfGuests")));

    const initialStart = searchParams.get("startDate")
        ? new Date(searchParams.get("startDate")!)
        : today;
    const initialEnd = searchParams.get("endDate")
        ? new Date(searchParams.get("endDate")!)
        : tomorrow;

    const [range, setRange] = useState<DateRange>({
        from: initialStart,
        to: initialEnd,
    })

    const handleConfirm = () => {
        if (!range.from || !range.to) return;
        setSearchParams(prev => {
            prev.set("startDate", range.from!.toLocaleDateString("en-CA"));
            prev.set("endDate", range.to!.toLocaleDateString("en-CA"));
            prev.set("numberOfGuests", String(numberOfGuests));
            return prev;
        })
        onClose();
    }//url업데이트

    return (
        <div className="p-4">
            <div className="flex items-center gap-2 mb-3 text-sm font-medium">
                <CalendarIcon />
                <span>{formatDate(range.from!)}~{formatDate(range.to!)}</span>
            </div>
            <div className="mb-4">
                <DayPicker
                    mode="range"
                    selected={range}
                    onSelect={(r) => r && setRange(r)}
                    /**
                     * 첫번째 클릭 후 r 상태
                     * from: Date, to:
                     */
                    locale={ko}
                    disabled={{ before: today }}
                />
            </div>
            <div className="flex items-center justify-between py-3 border-t border-gray-300">
                <span>인원</span>
                <div className="flex items-center gap-3">
                    <button
                        className="w-7 h-7 rounded-full hover:bg-gray-100 border border-gray-300"
                        onClick={() => setNumberOfGuests(prev => Math.max(1, prev - 1))}>-</button>
                    <span>{numberOfGuests}</span>
                    <button
                        className="w-7 h-7 rounded-full hover:bg-gray-100 border border-gray-300"
                        onClick={() => setNumberOfGuests(prev => prev + 1)}>+</button>
                </div>
            </div>
            <button className="w-full bg-gray-900 text-white rounded-lg py-3 mt-3 font-medium disabled:opacity-40 disabled:cursor-not-allowed"
                onClick={handleConfirm} disabled={!range.from || !range.to}>
                적용하기
            </button>
        </div>
    )
}