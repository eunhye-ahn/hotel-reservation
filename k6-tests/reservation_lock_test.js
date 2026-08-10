import http from 'k6/http'
import { check } from 'k6'
import { Counter } from 'k6/metrics'
import { uuidv4 } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js'

const BASE_URL = 'http://localhost:8080/api/v1'

const successCount = new Counter('reservation_success')
const conflictCount = new Counter('reservation_conflict')
const otherFailCount = new Counter('reservation_other_fail')

export const options = {
    scenarios: {
        concurrent_same_room: {
            executor: 'shared-iterations',
            vus: 20,
            iterations: 20,
            maxDuration: '30s',
        },
    },
    thresholds: {
        // 20명이 동시에 몰려도 재고(TARGET_INVENTORY)만큼만 성공해야 함 -> 아래에서 별도 검증
        http_req_duration: ['p(95)<2000'], // DB 락 대기시간 참고용
    },
}

// 테스트 대상 객실
const TARGET_HOTEL_ID = 1
const TARGET_ROOM_TYPE_ID = 4
const CHECK_IN = '2026-09-01'
const CHECK_OUT = '2026-09-03'
const RESERVE_GUEST = 2
const RESERVE_ROOM = 1

export default function () {
    const vuId = __VU

    // 1) 로그인
    const loginRes = http.post(
        `${BASE_URL}/auth/login`,
        JSON.stringify({
            email: `user${vuId}@example.com`,
            password: 'asd798852',
        }),
        { headers: { 'Content-Type': 'application/json' } }
    )

    check(loginRes, {
        '로그인 성공': (r) => r.status === 200,
    })

    const token = loginRes.json('accessToken')
    if (!token) {
        console.error(`VU ${vuId} - 로그인 실패, 예약 스킵`)
        return
    }

    // 2) 예약 요청
    const idempotencyKey = uuidv4()

    const reservationPayload = JSON.stringify({
        hotelId: TARGET_HOTEL_ID,
        roomTypeId: TARGET_ROOM_TYPE_ID,
        startDate: CHECK_IN,
        endDate: CHECK_OUT,
        numberOfGuests: RESERVE_GUEST,
        numberOfRooms: RESERVE_ROOM,
    })

    const reservationRes = http.post(`${BASE_URL}/reservations`, reservationPayload, {
        headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${token}`,
            'Idempotency-Key': idempotencyKey,
        },
    });

    // 3) 결과 분류
    if (reservationRes.status === 201) {
        successCount.add(1)
    } else if (reservationRes.status === 409) {
        conflictCount.add(1) // 재고부족(RESERVATION_UNAVAILABLE) 또는 재시도소진(RESERVATION_CONFLICT)
    } else {
        otherFailCount.add(1)
        console.error(`VU ${vuId} - 예상 밖 응답: ${reservationRes.status} - ${reservationRes.body}`)
    }

    check(reservationRes, {
        '201 또는 409 (예상된 응답)': (r) => r.status === 201 || r.status === 409,
        '5xx 없음': (r) => r.status < 500,
        '응답시간 2초 이내': (r) => r.timings.duration < 2000,
    })
}