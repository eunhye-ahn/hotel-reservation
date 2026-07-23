/**
 * zustand : 전역상태관리
 * token을 전역 페이지에서 공통관리
 */

import { create } from "zustand";
import { persist, createJSONStorage } from "zustand/middleware";

interface AuthStore {
    accessToken: string | null,
    role: string | null,

    setAccessToken: (token: string | null) => void,
    setRole: (role: string | null) => void,
    clearAccessToken: () => void
}


//create() : 클로저 생성(state, listeners 메모리에 저장)
//컴포넌트에서 useStore() 호출 => listeners 구독등록
//set() 호출  - state업데이트 -> listeneres 전체에 알림
//              -> 각 컴포넌트가 selector 재실행 - 값달라졌으면 리렌더링, 같으면 무시
export const useAuthStore = create<AuthStore>()(
    persist(
        (set) => ({
            accessToken: null,
            role: null,
            setAccessToken: (token) => set({ accessToken: token }),
            setRole: (role) => set({ role: role }),
            clearAccessToken: () => set({ accessToken: null, role: null })
        }),
        {
            name: "auth-storage",
            storage: createJSONStorage(() => localStorage),
        }
    )
);