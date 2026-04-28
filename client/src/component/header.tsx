import { useNavigate } from "react-router-dom"
import '@/component/header.css'
import { useAuthStore } from "@/store/useAuthStore"
import { logout } from "@/axios/api"

export const Header = () => {
    const navigate = useNavigate()
    const { accessToken, clearAccessToken } = useAuthStore();

    const handleLogout = () => {
        logout()
            .then(() => {
                clearAccessToken();
                navigate("/")
            })
    }

    return (
        <header className="header">
            <span className="header-logo" onClick={() => navigate("/")}>
                hotel_reserve
            </span>

            <nav className="header-nav">
                <span onClick={() => navigate("/")}>Home</span>
                {accessToken ?
                    <span onClick={handleLogout}>Logout</span>
                    : <span onClick={() => navigate("/login")}>Login</span>
                }
                <span onClick={() => navigate("/mypage")}>Mypage</span>
            </nav>
        </header>
    )
}