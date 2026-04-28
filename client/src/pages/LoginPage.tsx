import { useState } from "react"
import { login } from "../axios/api"
import { useNavigate } from "react-router-dom"
import { useAuthStore } from "../store/useAuthStore"
import type { LoginRequest } from "@/type/auth"
import '@/pages/LoginPage.css'

export const LoginPage = () => {
    const [form, setForm] = useState<LoginRequest>({
        email: "",
        password: ""
    })
    const navigate = useNavigate();
    const { setAccessToken } = useAuthStore();

    const handleLogin = () => {
        login(form)
            .then((res) => {
                console.log(res.data);
                setAccessToken(res.data.accessToken)
                navigate("/")
            }
            )
            .catch((err) => {
                alert(err.response.data.message)
            })
    }


    return (
        <div>
            <form>
                <div>
                    <label>email</label>
                    <input type="email"
                        onChange={(e) => setForm((prev) => ({ ...prev, email: e.target.value }))} />
                </div>
                <div>
                    <label>password</label>
                    <input type="password"
                        onChange={(e) => setForm((prev) => ({ ...prev, password: e.target.value }))} />
                </div>
                <button type="button" onClick={handleLogin}>Login</button>
                <button type="button" onClick={() => navigate("/signup")}>SignUp</button>
            </form>
        </div>
    )
}