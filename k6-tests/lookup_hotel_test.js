import http from 'k6/http'
import { check } from 'k6'
import { Counter } from 'k6/metrics'
import { uuidv4 } from 'https://jslib.k6.io/k6-utils/1.4.0/index.js'

const BASE_URL = 'http://localhost:8080/api/v1'

//
const successCount = new Counter('reservation_success')
const conflictCount = new Counter('reservation_conflict')
const otherFailCount = new Counter('reservation_other_fail')

export const options = {
    scenarios: {
        filter_hotel: {
            executor: 'shared-iterations',
            vus: 2000,
            iterations: 2000,
            maxDuration: '30s',
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<2000'], 
    },
}
///hotels/filter?q=%EC%84%9C%EC%9A%B8&lDongRegnCd=11&lDongSignguCd=170&startDate=2026-08-11&endDate=2026-08-12&numberOfGuests=2&numberOfRooms=1
//http://localhost:5173/hotels/list?lDongRegnCd=11&lDongSignguCd=170&startDate=2026-08-11&endDate=2026-08-12&numberOfGuests=3&numberOfRooms=1
// 테스트 대상 검색 조건
//
const q = ''
const lDongRegnCd = '11'
const lDongSignguCd = '170'
const lclsSystm2 = '1'
const startDate = '2026-08-11'
const endDate = '2026-08-12'
const numberOfGuests = '2'
const numberOfRooms = '1'
const page = '1'


export default function () {

    const params = {
        q,
        lDongRegnCd,
        lDongSignguCd,
        lclsSystm2,
        startDate,
        endDate,
        numberOfGuests,
        numberOfRooms,
        page
    }

    const queryString = Object.entries(params)
        .map(([k, v]) => `${k}=${encodeURIComponent(v)}`)
        .join('&')

    const filterRes = http.get(`${BASE_URL}/hotels/filter?${queryString}`, {
        headers: {
            'Content-Type': 'application/json'
        },
    });

    // 3) 결과 분류
    if (filterRes.status === 200) {
        successCount.add(1)
    } else {
        otherFailCount.add(1)
        console.error(`예상 밖 응답: ${filterRes.body}`)
    }

    check(filterRes, {
        '200': (f) => f.status === 200,
        '5xx 없음': (f) => f.status < 500,
        '응답시간 2초 이내': (f) => f.timings.duration < 2000,
    })
}