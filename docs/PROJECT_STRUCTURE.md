# 기술 스택

## 주요 기술 스택

| 분류 | 기술 |
| --- | --- |
| 언어 / 프레임워크 | Java 21, Spring Boot 3.5.14 |
| 인증 / 보안 | Spring Security, JWT (jjwt 0.12.3), OAuth2 Client |
| ORM / 쿼리 | Spring Data JPA, QueryDSL 5.0.0, MyBatis 3.0.3 |
| 토스서버 통신 | OpenFeign (Spring Cloud 2025.0.0) |
| 캐시 / 토큰관리 | Redis 7.4 |
| 검색 | Elasticsearch 8.19.0 (nori 형태소 분석기), Kibana 8.19.0 (모니터링) |
| 안정성 | Spring Retry |
| 빌드 | Gradle, dependency-management 플러그인 |
| 프론트엔드 | TypeScript, React, TanStack Query, Zustand, Axios |
| 인프라 / DevOps | GitHub, Docker, Docker Compose |
| 테스트 | JUnit5, Mockito, Postman |
| 에러관리 | Sentry |

## 데이터베이스

| 용도 | DB | 비고 |
| --- | --- | --- |
| 운영 DB | PostgreSQL 17 | Docker 컨테이너, 볼륨 영속화 |
| 테스트 DB | H2 | 인메모리, `runtimeOnly` |
| 캐시 | Redis 7.4 | 볼륨 영속화 |
| 검색 인덱스 | Elasticsearch 8.19.0 | nori 플러그인 설치, 단일 노드 |

## 참고

- **QueryDSL** : JPA 기반 동적 쿼리
- **MyBatis** : 복잡한 검색/집계가 있는 동적 쿼리
- **JPQL** : JPA 메서드명 길어질 때 가독성 저하 방지

## 패키지 구조

### 백엔드

```text
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

### 프론트엔드

```text
src/
├── api/                              # axios 인스턴스, 인터셉터 (전역 설정), api
│   ├── types/
│   ├── api.ts
│   ├── axiosInstance.ts
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
├── monitoring/                        # Sentry 설정
│   ├── CustomSeverityLevel.tsx
│   └── SentryNetworkError.tsx
│
├── App.tsx
├── index.css
└── main.tsx
```
