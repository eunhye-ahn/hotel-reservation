import { LoginPage } from './pages/LoginPage'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { MainPage } from './pages/MainPage'
import { SignUpPage } from './pages/SignUpPage'
import { Header } from './component/header'
import { useEffect } from 'react'
import { reissue } from './axios/api'
import { useAuthStore } from './store/useAuthStore'
import { HotelDetailPage } from './pages/HotelDetailPage'
import { ReservationPage } from './pages/ReservationPage'
import { ReservationConfirmPage } from './pages/ReservationConfirmPage'
import { MyPage } from './pages/MyPage'

function App() {
  const { setAccessToken } = useAuthStore();

  useEffect(() => {
    reissue()
      .then((res) => {
        setAccessToken(res.data.accessToken)
      })
      .catch(() => { })
  }, []);

  return (
    <BrowserRouter>
      <Header />
      <Routes>
        <Route path="/login" element={<LoginPage />} />
        <Route path="/" element={<MainPage />} />
        <Route path="/hotels/:hotelId" element={<HotelDetailPage />} />
        <Route path="/signup" element={<SignUpPage />} />
        <Route path="/hotels/:hotelId/rooms/:roomTypeId" element={<ReservationPage />} />
        <Route path="/reservations/:reservationKey" element={<ReservationConfirmPage />} />
        <Route path="/mypage" element={<MyPage />} />
      </Routes>
    </BrowserRouter>
  )
}

export default App
