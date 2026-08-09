import { ErrorMessage } from "@/common/component/ErrorMessage";
import { Spinner } from "@/common/component/Spinner";
import { useReservationDetail } from "@/features/admin/hooks/reservation/useReservationDetail";
import { useState } from "react";
import { useParams } from "react-router"
import { RoomAssignmentForm } from "../component/reservation/RoomAssignmentForm";
import { AssignRoomInfoTable } from "../component/reservation/AssignRoomInfoTable";
import { CancelByAdminForm } from "../component/reservation/CancelByAdminForm";

export const ReservationDetail = () => {
    const { id } = useParams<{ id: string }>()
    const reservationId = Number(id)

    const [showAssignForm, setShowAssignFrom] = useState<boolean>(false);
    const [showCancelForm, setShowCancelForm] = useState<boolean>(false);

    const { data, isLoading, isError } = useReservationDetail(reservationId)

    if (isLoading) return <Spinner />
    if (isError) return <ErrorMessage />
    console.log(reservationId)

    return (
        <div className="mx-auto ">
            <div className="flex items-baseline gap-2 mb-6">
                <h1 className="text-xl font-bold">예약 상세</h1>
                <span className="text-sm text-gray-400">
                    예약번호 {data?.displayReservationNO}
                </span>
            </div>

            {/* 기본정보 */}
            <div className="flex gap-6 items-stratch w-full">
                <div className="flex-1 border border-gray-200 overflow-hidden bg-white">
                    <div className="bg-gray-50 px-5 py-3 font-semibold text-sm border-b border-gray-200">
                        예약 기본 정보
                    </div>
                    <table className="w-full text-sm border border-gray-200 rounded-xl overflow-hidden">
                        <tbody className="divide-y divide-gray-100">
                            <tr>
                                <th className="w-28 text-left px-4 py-2.5 bg-gray-50 font-medium text-gray-500">예약자</th>
                                <td className="px-4 py-2.5">{data?.username}</td>
                            </tr>
                            <tr>
                                <th className="text-left px-4 py-2.5 bg-gray-50 font-medium text-gray-500">인원</th>
                                <td className="px-4 py-2.5">{data?.numberOfGuests}명</td>
                            </tr>
                            <tr>
                                <th className="text-left px-4 py-2.5 bg-gray-50 font-medium text-gray-500">호텔명</th>
                                <td className="px-4 py-2.5">{data?.hotelName}</td>
                            </tr>
                            <tr>
                                <th className="text-left px-4 py-2.5 bg-gray-50 font-medium text-gray-500">객실타입</th>
                                <td className="px-4 py-2.5">{data?.roomTypeName}</td>
                            </tr>
                            <tr>
                                <th className="text-left px-4 py-2.5 bg-gray-50 font-medium text-gray-500">기간</th>
                                <td className="px-4 py-2.5">{data?.startDate} ~ {data?.endDate}</td>
                            </tr>
                            <tr>
                                <th className="text-left px-4 py-2.5 bg-gray-50 font-medium text-gray-500">결제금액</th>
                                <td className="px-4 py-2.5 font-semibold">{data?.totalPrice}원</td>
                            </tr>
                        </tbody>
                    </table>
                    {/* 강제취소 */}
                    <section className="min-h-[52px] flex items-center">
                        {data?.reservationStatus != "BEFORE_USE" ? (
                            <p className="text-sm text-gray-400 mt-3 mx-1">이용 전 상태에서만 취소할 수 있습니다</p>
                        ) : (
                            <div className="">
                                <button
                                    className="px-4 py-2 text-sm text-red-500 border border-red-500 cursor-pointer hover:bg-gray-100 mt-1 mx-1"
                                    onClick={() => setShowCancelForm(!showCancelForm)}>{showCancelForm ? '취소' : '예약취소'}</button>
                                {showCancelForm &&
                                    <CancelByAdminForm
                                        reservationId={reservationId}
                                        paymentStatus={data.paymentStatus}
                                        refundPrice={data?.totalPrice} />
                                }
                            </div>
                        )}

                    </section>
                </div>
                {/* 배정상태 */}
                <div className="flex-1 border w-full border-gray-200 overflow-hidden bg-white flex flex-col">
                    <div className="bg-gray-50 px-5 py-3 font-semibold text-sm border-b border-gray-200">
                        배정상태
                    </div>
                    <table className="w-full text-sm">
                        <tbody className="divide-y divide-gray-100">
                            <tr>
                                <th className="w-28 text-left px-4 py-2.5 bg-gray-50 font-medium text-gray-500">예약상태</th>
                                <td className="px-4 py-2.5">{data?.reservationStatus}</td>
                            </tr>
                            <tr>
                                <th className="text-left px-4 py-2.5 bg-gray-50 font-medium text-gray-500">결제상태</th>
                                <td className="px-4 py-2.5">{data?.paymentStatus}</td>
                            </tr>
                            <tr>
                                <th className="text-left px-4 py-2.5 bg-gray-50 font-medium text-gray-500">배정여부</th>
                                <td className="px-4 py-2.5">
                                    <span className={`px-2 py-1 border rounded-md text-xs ${data?.roomAssigned
                                        ? "border-green-500 text-green-600"
                                        : "border-gray-300 text-gray-500"
                                        }`}>
                                        {data?.roomAssigned ? '배정완료' : '미배정'}
                                    </span>
                                </td>
                            </tr>
                            <tr>
                                <td colSpan={2}>
                                    <section className="mb-8">
                                        {data?.reservationStatus != "BEFORE_USE" || data?.paymentStatus != "PAID" ? (
                                            <p className="text-sm text-gray-400 mt-3 mx-3">배정할 수 없는 예약입니다</p>
                                        ) : data?.roomAssigned ? (
                                            <>
                                                <AssignRoomInfoTable
                                                    roomNumber={data?.roomNumber}
                                                    roomName={data?.roomName}
                                                    floor={data?.floor}
                                                    usable={data?.usable}
                                                />
                                                <button
                                                    className="px-4 py-2 text-sm text-red-500 border border-red-500 cursor-pointer hover:bg-gray-100 mt-5 mx-1"
                                                    onClick={() => setShowAssignFrom(!showAssignForm)}>{showAssignForm ? '접기' : '배정변경'}</button>
                                            </>
                                        ) : (
                                            <div>
                                                <button
                                                    className="px-4 py-2 text-sm text-red-500 border border-red-500 cursor-pointer hover:bg-gray-100 mt-5 mx-1"
                                                    onClick={() => setShowAssignFrom(!showAssignForm)}>{showAssignForm ? '취소' : '배정하기'}</button>

                                            </div>
                                        )

                                        }
                                    </section>

                                </td>
                            </tr>
                        </tbody>
                    </table>
                </div>
            </div>
            {/* 객실 배정 */}
            {showAssignForm &&
                <RoomAssignmentForm reservationId={reservationId} />
            }
        </div>
    )
}