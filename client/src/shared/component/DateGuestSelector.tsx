import { DayPicker } from "react-day-picker";
import type { DateRange } from "react-day-picker";
import "react-day-picker/style.css";
import { ko } from "date-fns/locale";
import { useState } from "react";
import { useSearchParams } from "react-router";
import '@/shared/component/DateGuestSelector.css'
import { CalendarIcon } from "lucide-react";

interface DateGuestSelector {
    onClose: () => void
}

export const DateGuestSelector = ({onClose}: DateGuestSelector) => {
        const today = new Date()
        const tomorrow = new Date(Date.now() + 86400000)
        const formatDate = (date: Date)=>{
            const mm = String(date.getMonth() +1).padStart(2,"0");
            const dd = String(date.getDate()).padStart(2, "0");
            const days = ["일", "월", "화", "수", "목", "금", "토"];
            return `${mm}.${dd}(${days[date.getDay()]})`;
        }

        const [searchParams, setSearchParams] = useSearchParams();
        const [numberOfGuests, setNumberOfGuests] = useState<number>(Number(searchParams.get("numberOfGuests")));

        const initialStart = searchParams.get("startDate") 
        ? new Date(searchParams.get("startDate")!)
        :today;
        const initialEnd = searchParams.get("endDate") 
        ? new Date(searchParams.get("endDate")!)
        :tomorrow;

        const [range, setRange] = useState<DateRange>({
            from: initialStart,
            to: initialEnd,
        })

        const handleConfirm = () => {
            if(!range.from || !range.to) return;
            setSearchParams(prev=>{
                prev.set("startDate", range.from!.toLocaleDateString("en-CA"));
                 prev.set("endDate", range.to!.toLocaleDateString("en-CA"));
                  prev.set("numberOfGuests", String(numberOfGuests));
                  return prev;
            })
            onClose();
        }//url업데이트

    return(
        <div className="date-guest-selector">
                <div className="date-summary">
                    <CalendarIcon />
                    <span>{formatDate(range.from!)}~{formatDate(range.to!)}</span>
                </div>
                <div className="calendar-wrapper">
                <DayPicker    
                    mode="range"
                    selected={range}
                    onSelect={(r)=>r && setRange(r)}
                    /**
                     * 첫번째 클릭 후 r 상태
                     * from: Date, to:
                     */
                    locale={ko}
                    disabled={{before:today}}
                />
            </div>
            <div className="guest-row">
                    <span>인원</span>
                    <div className="guest-counter">
                    <button onClick={()=>setNumberOfGuests(prev=>Math.max(1,prev-1))}>-</button>
                        <span>{numberOfGuests}</span>
                    <button onClick={()=>setNumberOfGuests(prev=>prev+1)}>+</button>
                    </div>
            </div>
            <button className="confirm-btn" onClick={handleConfirm} disabled={!range.from || !range.to}>
                적용하기
            </button>
        </div>
    )
}