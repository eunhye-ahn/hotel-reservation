import { useState } from "react"
import { login } from "../axios/api"
import { useNavigate } from "react-router-dom"
import { useAuthStore } from "../store/useAuthStore"
import type { LoginRequest } from "@/type/auth"
import '@/pages/LoginPage.css'
import { toast } from "react-toastify"

export const LoginPage = () => {
    const [form, setForm] = useState<LoginRequest>({
        email: '',
        password: ''
    });
    const navigate = useNavigate();
    const { setAccessToken } = useAuthStore();

    const handleLogin = () => {
        if (!form?.email || !form?.password) {
            toast.error('이메일과 비밀번호를 입력해주세요');
            return;
        }
        login(form)
            .then((res) => {
                console.log(res.data);
                setAccessToken(res.data.accessToken)
                navigate("/")
            }
            )
            .catch((err) => {
                toast.error(err.response.data.message)
            })
    }


    return (
        <div className="login-container">
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