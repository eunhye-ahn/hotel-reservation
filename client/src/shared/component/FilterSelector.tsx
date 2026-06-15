import { HOTEL_TYPES } from "@/constants/HotelType";
import { useState } from "react";
import { useSearchParams } from "react-router"
import '@/shared/component/FilterSelector.css'

interface FilterSelectorProps {
    onClose: () => void
}

export const FilterSelector = ({ onClose }: FilterSelectorProps) => {
    const [searchParams, setSearchParams] = useSearchParams();
    const lclsSystm2 = searchParams.get("lclsSystm2");


    const handleSelect = (code: string) => {
        setSearchParams(prev => {
            const next = new URLSearchParams(prev)
            if (next.get("lclsSystm2") === code) {
                next.delete("lclsSystm2")
            } else {
                next.set("lclsSystm2", code)
            }
            return next
        })
    }

    const handleConfirm = () => {
        onClose();
    }

    return (
        <div>
            <div className="hoteltype-row">
                <span>숙박 유형</span>
                <div>
                    {HOTEL_TYPES.map(type => (
                        <button key={type.code}
                            onClick={() => handleSelect(type.code)}
                            className={`type-button ${lclsSystm2 === type.code ? "active" : ""}`}
                        >
                            {type.name}
                        </button>
                    ))}
                </div>
            </div>
            <button className="confirm-btn" onClick={handleConfirm}>
                적용하기
            </button>
        </div>
    )
}