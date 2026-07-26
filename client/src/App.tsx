import { LoginPage } from './pages/LoginPage'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { MainPage } from './pages/MainPage'
import { SignUpPage } from './pages/SignUpPage'
import { HotelDetailPage } from './features/hotel/HotelDetailPage'
import { ReservationPage } from './pages/ReservationPage'
import { ReservationConfirmPage } from './pages/ReservationConfirmPage'
import { MyPage } from './pages/MyPage'
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import NotFoundPage from './pages/NotFoundPage'
import Layout from './Layout'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import PaymentSuccess from './pages/PaymentSuccess'
import PaymentFail from './pages/PaymentFail'
import { HotelListPage } from './features/hotel/HotelListPage'
import { RecentHotelPage } from './features/hotel/RecentHotelPage'
import { WishListPage } from './pages/WishListPage'
import { CollectionSelectModal } from '@/common/component/CollectionSelectModal'
import { AdminPage } from './features/admin/AdminPage'
import { ReservationDetail } from '@/features/admin/component/reservation/ReservationDetailPage'

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
    queries: {
      staleTime: 1000 * 60 * 5,
      retry: 1,
    }
  }
})

function App() {

  return (
    <QueryClientProvider client={queryClient}>
      <BrowserRouter>
        <ToastContainer position="top-right" autoClose={3000} />
        <CollectionSelectModal />
        <Routes>
          <Route path="/login" element={<LoginPage />} />
          <Route path="/signup" element={<SignUpPage />} />
          <Route path="/payments/success" element={<PaymentSuccess />} />
          <Route path="/payments/fail" element={<PaymentFail />} />
          <Route element={<Layout />}>
            <Route path="/" element={<MainPage />} />
            <Route path="/hotels/:hotelId" element={<HotelDetailPage />} />
            <Route path="/reservations/:reservationKey" element={<ReservationConfirmPage />} />
            {/* <Route path="/hotels/:hotelId/rooms/:roomTypeId" element={<ReservationPage />} /> */}
            <Route path="/reservations/:reservationKey/reservation-info" element={<ReservationPage />} />
            <Route path="/mypage" element={<MyPage />} />
            <Route path='/wishlists/:collectionId' element={<WishListPage />} />
            <Route path="/recent-hotel/list" element={<RecentHotelPage />} />
            <Route path="/hotels/list" element={<HotelListPage />} />
            <Route path="/admin" element={<AdminPage />} />
            <Route path="/admin/reservations/:id" element={<ReservationDetail />} />
          </Route>
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </BrowserRouter>
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  )
}

export default App
