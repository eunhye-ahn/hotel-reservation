import http from 'k6/http'
import { check } from 'k6'
import { Counter } from 'k6/metrics'

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080/api/v1'
 
//
const successCount = new Counter('filter_success')
const otherFailCount = new Counter('filter_fail')

export const options = {
    scenarios: {
        filter_hotel: {
            executor: 'shared-iterations',
            vus: 1,
            iterations: 1,
            maxDuration: '60s',
        },
    },
    thresholds: {
        http_req_duration: ['p(95)<2000'],
        checks: ['rate>0.95'],
    },
}

const regions = [
    { lDongRegnCd: '11', lDongSignguCd: '140' },
    { lDongRegnCd: '11', lDongSignguCd: '560' },
    { lDongRegnCd: '11', lDongSignguCd: '740' }, 
    { lDongRegnCd: '11', lDongSignguCd: '500' }, 
]

const dateOffsets = [
    { startDate: '2026-08-11', endDate: '2026-08-12' },
    { startDate: '2026-08-15', endDate: '2026-08-16' },
    { startDate: '2026-08-20', endDate: '2026-08-22' },
]

const q = '서울'
const lclsSystm2 = '1'
const numberOfGuests = '2'
const numberOfRooms = '1'
const page = '1'

// 실패 로그 폭주 방지용 최대 출력 개수
const MAX_ERROR_LOGS = 20
let errorLogCount = 0

export default function () {

    const region = regions[Math.floor(Math.random() * regions.length)]
    const dates = dateOffsets[Math.floor(Math.random() * dateOffsets.length)]

    const params = {
        q,
        lDongRegnCd: region.lDongRegnCd,
        lDongSignguCd: region.lDongSignguCd,
        lclsSystm2,
        startDate: dates.startDate,
        endDate: dates.endDate,
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
        if (errorLogCount < MAX_ERROR_LOGS) {
            console.error(`예상 밖 응답: ${filterRes.body}`)
            errorLogCount++
        }
    }

    check(filterRes, {
        '200': (f) => f.status === 200,
        '5xx 없음': (f) => f.status < 500,
        '응답시간 2초 이내': (f) => f.timings.duration < 2000,
    })
}