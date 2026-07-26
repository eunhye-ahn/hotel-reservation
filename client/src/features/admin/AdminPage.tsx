
import { ReservationList } from "@/features/admin/component/reservation/ReservationList"
import { PaymentList } from "./component/payment/PaymentList"
import { SettlementList } from "./component/settlement/SettlementList"

export function AdminPage() {
    //메뉴바

    //룸배정 및 예약확정


    //예약현황조회

    //결제/정산기록

    //정산리포트

    return (
        <>
            <ReservationList />
            <br/>
            <PaymentList />
            <br/>
            <SettlementList />
        </>
    )
}