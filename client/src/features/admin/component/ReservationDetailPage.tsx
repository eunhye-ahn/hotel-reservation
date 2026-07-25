import { ErrorMessage } from "@/component/common/ErrorMessage";
import { Spinner } from "@/component/common/Spinner";
import { useReservationDetail } from "@/features/admin/hooks/useReservationDetail";
import { useState } from "react";
import { useParams } from "react-router"
import { RoomAssignmentForm } from "./RoomAssignmentForm";

export const ReservationDetail = () => {
    const { id } = useParams<{ id: string }>()
    const reservationId = Number(id)

    const [showAssignForm, setShowAssignFrom] = useState<boolean>(false);
    const [showCancelForm, setShowCancelForm] = useState<boolean>(false);

    const { data, isLoading, isError } = useReservationDetail(reservationId)

    if (isLoading) return <Spinner />
    if (isError) return <ErrorMessage />

    return (
        <div>
            <h1>예약 상세</h1>

            {/* 기본정보 */}
            <section>
                <h2>예약 기본 정보</h2>
                <div className="grid grid-cols-2">
                    <span>예약자</span>
                    <span>{data?.username}</span>
                    <span>인원</span>
                    <span>{data?.numberOfGuests}</span>
                    <span>호텔명</span>
                    <span>{data?.hotelName}</span>
                    <span>객실명</span>
                    <span>{data?.roomTypeName}</span>
                    <span>기간</span>
                    <span>{data?.startDate}~{data?.endDate}</span>
                    <span>결제금액</span>
                    <span>{data?.totalPrice}원</span>
                </div>
            </section>
            {/* 배정상태 */}
            <section>
                <h2>배정상태</h2>
                <div className="grid grid-cols-2">
                    <span>예약상태</span>
                    <span>{data?.reservationStatus}</span>
                    <span>결제상태</span>
                    <span>{data?.paymentStatus}</span>
                    <span>배정여부</span>
                    <span>{ }</span>
                </div>
            </section>
            {/* 객실 배정 */}
            <section>
                <h2>객실 배정</h2>
                {data?.reservationStatus != "BEFORE_USE" ? (
                    <p>이용 전 상태에서만 배정이 가능합니다</p>
                ) : data?.roomAssigned ? (
                    <>
                        <p>배정 정보</p>
                        {!showAssignForm ?
                            (<button>배정변경</button>) : (
                                <>
                                    {/* 재배정폼 */}
                                </>
                            )
                        }
                    </>
                ) : (
                    !showAssignForm ? (
                        <button onClick={() => setShowAssignFrom(true)}>배정하기</button>
                    ) : (
                        <RoomAssignmentForm reservationId={reservationId} />
                    )
                )

                }
            </section>
            {/* 강제취소 */}
            <section>
                {data?.reservationStatus != "BEFORE_USE" ? (
                    <p>이용 전 상태에서만 취소할 수 있습니다</p>
                ) : (
                    !showCancelForm ? (
                        <button>강제취소</button>
                    ) : (
                        <>{/* 강제취소폼 */}</>
                    )
                )}

            </section>
        </div>
    )
}