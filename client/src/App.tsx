import { LoginPage } from './pages/LoginPage'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { MainPage } from './pages/MainPage'
import { SignUpPage } from './pages/SignUpPage'
import { useEffect } from 'react'
import { HotelDetailPage } from './pages/HotelDetailPage'
import { ReservationPage } from './pages/ReservationPage'
import { ReservationConfirmPage } from './pages/ReservationConfirmPage'
import { MyPage } from './pages/MyPage'
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import NotFoundPage from './pages/NotFoundPage'
import Layout from './Layout'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import { reissue } from './api/reservation-service'
import { useAuthStore } from './store/useAuthStore'
import PaymentSuccess from './pages/PaymentSuccess'
import PaymentFail from './pages/PaymentFail'
import { HotelListPage } from './pages/HotelListPage'
import { RecentHotelPage } from './pages/RecentHotelPage'

/**
 * [tanstack query 흐름] : 서버 상태관리 라이브러리 : 비동기데이터
 * 
 * 1. QueryClient 생성
 * - 앱전체의 캐시 저장소
 * - staleTime: 5분 -> 5분 안에 같은 요청오면 api 호출 없이 캐시 사용
 * - retry: 1 -> 
 */

const queryClient = new QueryClient({
  defaultOptions: {
    queries:{
      staleTime: 1000*60*5,
      retry: 1,
    }
  }
})

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
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <ToastContainer position="top-right" autoClose={3000} />
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignUpPage />} />
          <Route path="/payments/success" element={<PaymentSuccess />}/>
          <Route path="/payments/fail" element={<PaymentFail />}/>
          <Route element={<Layout />}>
            <Route path="/" element={<MainPage />} />
            <Route path="/hotels/:hotelId" element={<HotelDetailPage />} />
            <Route path="/reservations/:reservationKey" element={<ReservationConfirmPage/>}/>
            {/* <Route path="/hotels/:hotelId/rooms/:roomTypeId" element={<ReservationPage />} /> */}
            <Route path="/reservations/:reservationKey/reservation-info" element={<ReservationPage />} />
            <Route path="/mypage" element={<MyPage />} />
            <Route path="/recent-hotel/list" element={<RecentHotelPage />} />
            <Route path="/hotels/list" element={<HotelListPage />} />
          </Route>
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </BrowserRouter>
      <ReactQueryDevtools initialIsOpen={false}/>
    </QueryClientProvider>
  )
}

export default App
