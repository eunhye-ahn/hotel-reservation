import { LoginPage } from './pages/LoginPage'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { MainPage } from './pages/MainPage'
import { SignUpPage } from './pages/SignUpPage'
import { Header } from './component/header'
import { useEffect } from 'react'
import { reissue } from './axios/api'
import { useAuthStore } from './store/useAuthStore'

function App() {
  const { setAccessToken } = useAuthStore();

  useEffect(() => {
    reissue()
      .then((res) => {
        setAccessToken(res.data.accessToken)
      })
  }, []);

  return (
    <BrowserRouter>
      <Header />
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/" element={<MainPage />} />
        <Route path="/signup" element={<SignUpPage />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
