import { useState } from "react";

export const useHotelListModals = () => {
    const [isDateOpen, setIsDateOpen] = useState<boolean>(false)
    const [isFilterOpen, setIsFilterOpen] = useState<boolean>(false)

    return { isDateOpen, setIsDateOpen, isFilterOpen, setIsFilterOpen }
}