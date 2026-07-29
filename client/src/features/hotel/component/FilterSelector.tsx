import { HOTEL_TYPES } from "@/type/HotelType";
import { useState } from "react";
import { useSearchParams } from "react-router"

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
            <div className="p-4">
                <div className="mb-4">
                    <p className="font-medium text-sm mb-3">숙박 유형</p>
                    <div className="flex flex-wrap gap-2">
                        {HOTEL_TYPES.map(type => (
                            <button key={type.code}
                                onClick={() => handleSelect(type.code)}
                                className={`
                                px-4 py-2 rounded-full border text-sm cursor-pointer
                                type-button ${lclsSystm2 === type.code ? "bg-gray-900 text-white border-gray-900"
                                        : "border-gray-300 hover:bg-gray-100"}`}
                            >
                                {type.name}
                            </button>
                        ))}
                    </div>
                </div>
            </div>
            <button className="w-full bg-gray-900 text-white rounded-lg font-medium cursor-pointer py-2"
                onClick={handleConfirm}>
                적용하기
            </button>
        </div>
    )
}