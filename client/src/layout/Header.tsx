import { useLocation, useNavigate } from "react-router-dom"
import { useAuthStore } from "@/store/useAuthStore"
import { logout } from "@/api/api"
import { SearchBar } from "@/features/hotel/component/SearchBar"
import styles from '@/layout/Header.module.css'
import { useQueryClient } from "@tanstack/react-query"

export const Header = () => {
    const navigate = useNavigate()
    const { accessToken, clearAccessToken, role } = useAuthStore()
    const location = useLocation()
    const isMainPage = location.pathname === "/"
    const queryClient = useQueryClient()
    const handleLogout = () => {
        logout()
            .then(() => {
                clearAccessToken();
                queryClient.clear()
                navigate("/")
            })
    }

    return (
        <header className={`${styles.header} ${isMainPage ? '' : styles.headerSolid}`}>
            <div className={styles.inner}>
                <span className={styles.logo} onClick={() => navigate("/")}>
                    STAY N 테스트
                </span>
                <div className={styles.searchWrap}>
                    <SearchBar />
                </div>
                <nav style={{ display: 'flex', gap: '1rem' }}>
                    {accessToken ?
                        <span className={styles.btn} onClick={handleLogout}>Logout</span>
                        : <span className={styles.btn} onClick={() => navigate("/login")}>Login</span>
                    }
                    <span className={styles.btn} onClick={() => navigate("/mypage")}>Mypage</span>
                    <span className={styles.btn} onClick={() => navigate("/recent-hotel/list")}>최근본상품</span>
                    {accessToken && role == "ROLE_ADMIN" &&
                        <span className={styles.btn} onClick={() => navigate("/admin")}>관리자</span>
                    }
                </nav>
            </div>
        </header>
    )
}