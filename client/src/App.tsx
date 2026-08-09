import { LoginPage } from './features/auth/LoginPage'
import { BrowserRouter, Route, Routes } from 'react-router-dom'
import { MainPage } from './features/hotel/pages/MainPage'
import { SignUpPage } from './features/auth/SignUpPage'
import { HotelDetailPage } from './features/hotel/pages/HotelDetailPage'
import { ReservationPage } from './features/reservation/pages/ReservationPage'
import { ReservationConfirmPage } from './features/reservation/pages/ReservationConfirmPage'
import { MyPage } from '@/features/mypage/pages/MyPage'
import { ToastContainer } from 'react-toastify';
import 'react-toastify/dist/ReactToastify.css';
import NotFoundPage from './common/pages/NotFoundPage'
import Layout from './layout/Layout'
import { QueryClient, QueryClientProvider } from '@tanstack/react-query'
import { ReactQueryDevtools } from '@tanstack/react-query-devtools'
import PaymentSuccess from './features/reservation/pages/PaymentSuccess'
import PaymentFail from './features/reservation/pages/PaymentFail'
import { HotelListPage } from './features/hotel/pages/HotelListPage'
import { RecentHotelPage } from './features/hotel/pages/RecentHotelPage'
import { WishListPage } from './features/mypage/pages/WishListPage'
import { CollectionSelectModal } from '@/common/component/CollectionSelectModal'
import { ReservationDetail } from '@/features/admin/pages/ReservationDetailPage'
import AdminLayout from './features/admin/component/AdminLayout'
import { SettlementHistory } from './features/admin/pages/SettlementHistory'
import { InventorySummaryList } from './features/admin/pages/InventorySummaryList'
import { InventoryDetail } from './features/admin/pages/InventoryDetail'
import { ReservationList } from './features/admin/pages/ReservationList'
import { PaymentList } from './features/admin/pages/PaymentList'
import { SettlementList } from './features/admin/pages/SettlementList'
import { AdminDashBoard } from './features/admin/pages/AdminDashBoard'
import { OAuth2RedirectPage } from './features/auth/OAuth2RedirectPage'

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
          <Route path="/payments/success" element={<PaymentSuccess />} />
          <Route path="/payments/fail" element={<PaymentFail />} />
          <Route element={<Layout />}>
            <Route path="/login" element={<LoginPage />} />
            <Route path="/oauth2/redirect" element={<OAuth2RedirectPage />} />
            <Route path="/signup" element={<SignUpPage />} />
            <Route path="/" element={<MainPage />} />
            <Route path="/hotels/:hotelId" element={<HotelDetailPage />} />
            <Route path="/reservations/:reservationKey" element={<ReservationConfirmPage />} />
            {/* <Route path="/hotels/:hotelId/rooms/:roomTypeId" element={<ReservationPage />} /> */}
            <Route path="/reservations/:reservationKey/reservation-info" element={<ReservationPage />} />
            <Route path="/mypage" element={<MyPage />} />
            <Route path='/wishlists/:collectionId' element={<WishListPage />} />
            <Route path="/recent-hotel/list" element={<RecentHotelPage />} />
            <Route path="/hotels/list" element={<HotelListPage />} />
            <Route path="/admin" element={<AdminLayout />}>
              <Route index element={<AdminDashBoard />} />
              <Route path="/admin/reservations/:id" element={<ReservationDetail />} />
              <Route path="/admin/reservations" element={<ReservationList />} />
              <Route path="/admin/payments" element={<PaymentList />} />
              <Route path="/admin/settlements" element={<SettlementList />} />
              <Route path="/admin/settlements/:hotelId" element={<SettlementHistory />} />
              <Route path="/admin/inventory" element={<InventorySummaryList />} />
              <Route path="/admin/inventory/:hotelId" element={<InventoryDetail />} />
            </Route>
          </Route>
          <Route path="*" element={<NotFoundPage />} />
        </Routes>
      </BrowserRouter>
      <ReactQueryDevtools initialIsOpen={false} />
    </QueryClientProvider>
  )
}

export default App
