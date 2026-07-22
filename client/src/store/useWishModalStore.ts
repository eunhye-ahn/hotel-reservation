import { create } from "zustand"

interface WishModalState {
    isOpen: boolean
    hotelId: number | null
    open: (hotelId: number) => void
    close: () => void
}

export const useWishModalStore = create<WishModalState>((set) => ({
    isOpen: false,
    hotelId: null,
    open: (hotelId) => set({ isOpen: true, hotelId }),
    close: () => set({ isOpen: false, hotelId: null }),
}))