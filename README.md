# 프로젝트 소개
| 항목 | 내용 |
|---|---|
| 프로젝트명 | 스테이엔 (StayN) |
| 한 줄 소개 | **숙박 예약 · 결제 · 정산 플랫폼** |
| 개발 목적 | **실무 감각**을 익히기 위해 실무 환경을 가정하고, 예약-결제-정산으로 이어지는 흐름을 직접 설계·구현·배포 |
| 개발 기간 | 2026.04 ~ 2026.08 (5개월) |
| 개발 인원 | 1인 (백엔드/프론트엔드/배포 개인 개발) |
| 배포 URL | https://stayn.store |

# 사용한 기술

| 구분 | 기술/버전 |
|---|---|
| Backend | Spring Boot 3.5.14, Java 21, Spring Data JPA, MyBatis Spring Boot Starter 3.0.3, QueryDSL 5.0.0, Spring Security, JWT, OAuth2 |
| Database | PostgreSQL 17, Redis 7.4, Elasticsearch 8.19 |
| Frontend | React 19.2.5, TypeScript 6.0.2, TanStack Query, Vite |
| Infra | Docker, AWS (EC2, RDS, ARM, SSM, CludFront, S3, Route53), Nginx |
| CI/CD | GitHub Actions |
| Monitoring | Actuator + Prometheus, Sentry |

# 주요 기능
## 유저
- 숙박시설 검색/조회
- 임시 예약 홀드
- 예약 취소
- 예약 조회
- 북마크
- 최근 본 숙박시설 조회

## 관리자
- 예약/결제/정산 조회
- 예약 취소(결제 취소 연동)
- 수동 정산

## 시스템 (자동화)
- 정산 스케줄러: 새벽 자동 정산 처리
- 예약 상태 처리: 체크아웃 완료 기준 이용전->이용후 일괄 전환

# 주요 실행화면
<table>
  <tr>
    <th align="center">메인</th>
    <th align="center">객실 조회</th>
  </tr>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/8e57c2d8-b5e7-4653-a0ab-24e5521fd058" width="400">
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/d481d87b-2f42-459e-8747-09ab9346e608" width="400">
    </td>
  </tr>
  <tr>
    <th align="center">관리자 대시보드</th>
    <th align="center">예약 관리 및 객실 배정</th>
  </tr>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/5099de0a-c6b2-4a85-bfec-3d0cb2545d6f" width="400">
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/7a153edd-6e02-46c8-9c60-8d1170993a9f" width="400">
    </td>
  </tr>
  <tr>
    <th align="center">수동 정산</th>
    <th align="center">전체 예약 조회</th>
  </tr>
  <tr>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/67bd9aa1-502f-498b-a427-e1bd80e4c5bf" width="400">
    </td>
    <td align="center">
      <img src="https://github.com/user-attachments/assets/641e756b-021c-4463-9290-1c4c0aca1939" width="400">
    </td>
  </tr>
</table>

# 인프라 아키텍처
<img width="3796" height="2697" alt="AWS cloud diagram (Community)" src="https://github.com/user-attachments/assets/8b2b9e75-bc9c-4683-bd04-3353f1ee2d6d" />

# 실행 방법
## 백엔드
```powershell
docker compose -f docker-compose.yml --profile local up -d
./gradlew bootRun
```
## 프론트엔드
```powershell
npm install
npm run dev
```

# 패키지 구조
## 백엔드
```
com.hotel.hotelreservation/src
├── common/                              # 공유계층
│   ├── domain/                          # 생성일/수정일 공유 도메인
│   ├── config/                         
│   ├── exception/                       # 공통 예외
│   ├── idempotency/                     # 멱등키
│   ├── auth/                            # JWT 
│   └── util/                            # 쿠키(저장/삭제)
│
├── user/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── domain/
│   └── dto/
│
├── hotel/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── domain/
│   ├── dto/
│   ├── mapper/
│
├── reservation/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── domain/
│   ├── dto/
│   └── mapper/
│
├── payment/
│   ├── controller/
│   ├── service/
│   ├── repository/
│   ├── domain/
│   ├── mapper/
│   ├── dto/
│   └── client/                          # Feign 기반 Toss Payments 결제 승인/취소 요청 연동
│
└── admin/
    ├── controller/
    ├── mapper/
    ├── service/                        
    └── dto/
```

## 프론트엔드
```
src/
├── api/                             
│   ├── types/                        # api response request 타입 정의
│   ├── api.ts                        # axios를 통한 api모음
│   ├── axiosInstance.ts              # axios 인스턴스, 인터셉터
│   ├── CustomSeverityLevel.tsx       # Sentry 모니터링
│   ├── SentryNetworkError.tsx        # Sentry 모니터링
│   └── errorHelpers.ts
│
├── asset/                            # 이미지
│
├── common/
│   ├── components/                   # 공통 컴포넌트 (Button, Modal 등)
│   └── pages/                        # 404페이지
│
├── features/                         # 도메인별 기능 단위
│   ├── admin/
│   │   ├── hooks/
│   │   ├── components/
│   │   └── pages/
│   │
│   ├── auth/
│   │   ├── hooks/
│   │   ├── components/
│   │   └── pages/
│   │
│   ├── hotel/
│   │   ├── hooks/
│   │   ├── components/
│   │   └── pages/
│   │
│   ├── mypage/
│   │   ├── hooks/
│   │   ├── components/
│   │   └── pages/
│   │
│   └── reservation/
│       ├── hooks/
│       ├── components/
│       └── pages/
│
├── layout/                           # Header, Footer 등 레이아웃 컴포넌트
│
├── store/                            # Zustand 스토어
│   ├── authStore.ts
│   ├── recentHotelStore.ts
│   ├── wishModalStore.ts
│   └── regionStore.ts
│
├── ui/                                
│   ├── button.tsx
│   ├── utils.tsx
│   └── dialog.tsx
│
│
├── App.tsx
├── index.css
└── main.tsx
```
