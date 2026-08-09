import { useEffect } from "react"
import { useNavigate, useSearchParams } from "react-router-dom"
import { useAuthStore } from "@/store/useAuthStore"
import type { CustomJwtPayLoad } from "@/api/types/auth"
import { jwtDecode } from "jwt-decode"
import { toast } from "react-toastify"

export const OAuth2RedirectPage = () => {
    const navigate = useNavigate()
    const [searchParams] = useSearchParams()
    const { setAccessToken, setRole } = useAuthStore()

    useEffect(() => {
        const accessToken = searchParams.get("accessToken")

        if (!accessToken) {
            toast.error("로그인에 실패했습니다")
            navigate("/login", { replace: true })
            return
        }

        setAccessToken(accessToken)
        const decoded = jwtDecode<CustomJwtPayLoad>(accessToken)
        setRole(decoded.role)
        navigate("/", { replace: true })
    }, [])

    return (
        <div className="min-h-screen flex items-center justify-center">
            <p className="text-sm text-gray-900">로그인 처리 중...</p>
        </div>
    )
}