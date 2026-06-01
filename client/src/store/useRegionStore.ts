import { create } from "zustand";
import { persist } from "zustand/middleware";

type RegionState = {
    regionCode?: string;
    subRegionCode?: string;
    displayName: string;
    setRegion: (displayName: string, regionCode?: string,  subRegionCode?: string) => void;
     resetRegion: () => void;
}

export const useRegionStore = create<RegionState>()(
    //persist: localStorage에 저장 -> 새로고침해도 유지
    persist(
        (set)=>({
            regionCode: undefined,
            subRegionCode: undefined,
            displayName: "",
            //set: zustand가 제공하는 상태업데이트함수
            setRegion: (displayName, regionCode,subRegionCode) =>
                set({ regionCode, subRegionCode, displayName }),
            resetRegion: () => set({ regionCode: undefined, subRegionCode: undefined, displayName: "" }),
        }),
        {name: "region-storage"} //localStorage에 저장될 키 이름
    )
)