/**
 * zustand : 전역상태관리
 * token을 전역 페이지에서 공통관리
 */

import { create } from "zustand";

//스토어 타입 정의
//상태와 액션을 하나의 인터페이스로 관리
interface AuthStore {
    accessToken: string | null,

    setAccessToken: (token: string|null)=>void,
    clearAccessToken: () => void
}

//create() : 클로저 생성(state, listeners 메모리에 저장)
//컴포넌트에서 useStore() 호출 => listeners 구독등록
//set() 호출  - state업데이트 -> listeneres 전체에 알림
//              -> 각 컴포넌트가 selector 재실행 - 값달라졌으면 리렌더링, 같으면 무시
export const useAuthStore = create<AuthStore>(
    (set)=>({
        accessToken: null,
        setAccessToken: (token) => set({ accessToken: token }),
        clearAccessToken: () => set({ accessToken: null })
    })
);