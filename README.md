# 프로젝트 소개
| 항목 | 내용 |
|---|---|
| 프로젝트명 | 스테이엔 (StayN) |
| 한 줄 소개 | **숙박 예약 · 결제 · 정산 플랫폼** |
| 개발 목적 | **실무 감각**을 익히기 위해 실무 환경을 가정하고, 예약-결제-정산으로 이어지는 흐름을 직접 설계·구현·배포 |
| 개발 기간 | 2026.04 ~ 2026.08 (4개월) |
| 개발 인원 | 1인 (백엔드/프론트엔드/배포 개인 개발) |

# 사용한 기술

| 구분 | 기술/버전 |
|---|---|
| Backend | Spring Boot 3.5.14, Java 21, Spring Data JPA, MyBatis Spring Boot Starter 3.0.3, QueryDSL 5.0.0, Spring Security, JWT, OAuth2 |
| Database | PostgreSQL 17, Redis 7.4, Elasticsearch 8.19 |
| Frontend | React 19.2.5, TypeScript 6.0.2, TanStack Query, Vite |
| Infra | Docker, AWS (EC2), Nginx |
| CI/CD | GitHub Actions |
| Monitoring | Actuator + Prometheus, Sentry |

# 주요 실행화면
<img width="1917" height="1017" alt="image (14)" src="https://github.com/user-attachments/assets/8e57c2d8-b5e7-4653-a0ab-24e5521fd058" />
<img width="1896" height="1026" alt="image" src="https://github.com/user-attachments/assets/d481d87b-2f42-459e-8747-09ab9346e608" />
<img width="1902" height="1028" alt="image (1)" src="https://github.com/user-attachments/assets/5099de0a-c6b2-4a85-bfec-3d0cb2545d6f" />
<img width="1894" height="1031" alt="image (2)" src="https://github.com/user-attachments/assets/7a153edd-6e02-46c8-9c60-8d1170993a9f" />
<img width="1904" height="1032" alt="image (3)" src="https://github.com/user-attachments/assets/67bd9aa1-502f-498b-a427-e1bd80e4c5bf" />
<img width="1899" height="1028" alt="image (4)" src="https://github.com/user-attachments/assets/641e756b-021c-4463-9290-1c4c0aca1939" />

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
npm run dev
```

# 패키지 구조
## 백엔드
```
com.hotel.hotelreservation/src
├── common/                              # 공유계층
│   ├── domain/                          # 생성일/수정일 공유 도메인
│   ├── config/                          # SecurityConfig, RedisConfig, (ESConfig), QueryDSL Config 등
│   ├── exception/                       # 공통 예외
│   ├── idempotency/                     # 멱등키
│   ├── auth/                            # JWT 필터
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
│   └── (search)/                        # HotelDocument, ES 관련 (인덱싱, nori)
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
│   └── client/                          # Toss Payments 결제 승인/취소 요청 API
│
└── admin/
    ├── controller/
    ├── mapper/
    ├── service/                         # 여러 도메인 조회/집계
    └── dto/
```

## 프론트엔드
```
src/
├── api/                              # axios 인스턴스, 인터셉터 (전역 설정), api
│   ├── types/
│   ├── api.ts
│   ├── axiosInstance.ts
│   ├── CustomSeverityLevel.tsx       # Sentry 모니터링
│   ├── SentryNetworkError.tsx        # Sentry 모니터링
│   └── errorHelpers.ts
│
├── asset/                            # 이미지, 폰트 등 정적 리소스
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
├── layout/                           # Header, Footer, Sidebar 등 레이아웃 컴포넌트
│
├── store/                            # Zustand 스토어
│   ├── authStore.ts
│   ├── recentHotelStore.ts
│   ├── wishModalStore.ts
│   └── regionStore.ts
│
├── ui/                                # 순수 UI 프리미티브 (디자인 시스템 컴포넌트)
│   ├── button.tsx
│   ├── utils.tsx
│   └── dialog.tsx
│
│
├── App.tsx
├── index.css
└── main.tsx
```
