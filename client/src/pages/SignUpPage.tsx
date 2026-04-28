import { useState } from "react"
import { useNavigate } from "react-router-dom";
import { useAuthStore } from "../store/useAuthStore";
import { signUp } from "../axios/api";
import type { SignUpRequest } from "@/type/auth";

export const SignUpPage = () => {
    const [form, setForm] = useState<SignUpRequest>({
        name: "",
        email: "",
        password: "",
        phone: ""
    })
    const navigate = useNavigate();
    const { setAccessToken } = useAuthStore();

    const handleSignUp = () => {
        console.log(form);
        signUp(form)
            .then((res) => {
                setAccessToken(res.data.accessToken)
                navigate("/")
            })
            .catch((err) => {
                alert(err.response.data.message)
            })
    }

    return (
        <div>
            <form>
                <div>
                    <label>name</label>
                    <input type="text"
                        onChange={(e) => setForm((prev) => ({ ...prev, name: e.target.value }))} />
                </div>
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
                <div>
                    <label>phone</label>
                    <input type="tel" maxLength={11}
                        onChange={(e) => setForm((prev) => ({ ...prev, phone: e.target.value }))} />
                </div>
                <button type="button" onClick={handleSignUp}>SignUp</button>
                <button type="button" onClick={() => navigate("/")}>취소</button>
            </form>
        </div>
    )
}