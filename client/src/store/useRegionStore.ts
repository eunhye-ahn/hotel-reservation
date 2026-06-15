import type { Region, SubRegion } from "@/constants/Region";
import { create } from "zustand";
import { persist } from "zustand/middleware";

type RecentRegion = {
    code: string;
    name: string;
    subRegion?: {
        code: string;
        name: string;
    }
}


interface RegionState {
    regionCode?: string;
    subRegionCode?: string;
    displayName: string;
    recentRegions: RecentRegion[];
    setRegion: (displayName: string, regionCode?: string,  subRegionCode?: string) => void;
     resetRegion: () => void;
     saveRecentRegion: (region: Region, subRegion?: SubRegion) => void;
    removeRecentRegion: (code:string) => void;
}

export const useRegionStore = create<RegionState>()(
    //persist: localStorage에 저장 -> 새로고침해도 유지
    persist(
        (set, get)=>({
            regionCode: undefined,
            subRegionCode: undefined,
            displayName: "",
            recentRegions: [],
            //set: zustand가 제공하는 상태업데이트함수
            setRegion: (displayName, regionCode,subRegionCode) =>
                set({ regionCode, subRegionCode, displayName }),
            resetRegion: () => set({ regionCode: undefined, subRegionCode: undefined, displayName: "" }),
            saveRecentRegion: (region, subRegion)=>{
                const slim: RecentRegion = {
                    code: subRegion ? subRegion.code : region.code,
                    name: subRegion ? subRegion.name : region.name,
                }
                 //중복제거
                const filtered = get().recentRegions.filter(r=>r.code !== slim.code)    
                //맨앞에 추가, 5개제한
                const updated = [slim, ...filtered].slice(0,5)
                set({recentRegions: updated})
            },
            removeRecentRegion: (code)=>{
                const updated = get().recentRegions.filter(r=>r.code !== code)
                set({recentRegions: updated})
            }
        }),
        {name: "region-storage"} //localStorage에 저장될 키 이름
    )
)