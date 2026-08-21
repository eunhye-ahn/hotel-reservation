# 프로젝트 소개
| 항목 | 내용 |
|---|---|
| 프로젝트명 | 스테이엔 (StayN) |
| 한 줄 소개 | **숙박 예약 · 결제 · 정산 플랫폼** |
| 개발 목적 | **실무 감각**을 익히기 위해 실무 환경을 가정하고, 예약-결제-정산으로 이어지는 흐름을 직접 설계·구현·배포 |
| 개발 기간 | 2026.04 ~ 2026.08 (4개월) |
| 개발 인원 | 1인 (백엔드/프론트엔드/배포 개인 개발) |

# 사용한 기술

## 백엔드
![Spring](https://img.shields.io/badge/spring-%236DB33F.svg?style=for-the-badge&logo=spring&logoColor=white) ![Java](https://img.shields.io/badge/java-%23ED8B00.svg?style=for-the-badge&logo=openjdk&logoColor=white)

## 프론트엔드
![React](https://img.shields.io/badge/react-%2320232a.svg?style=for-the-badge&logo=react&logoColor=%2361DAFB) ![TypeScript](https://img.shields.io/badge/typescript-%23007ACC.svg?style=for-the-badge&logo=typescript&logoColor=white)

## 데이터베이스
![Postgres](https://img.shields.io/badge/postgres-%23316192.svg?style=for-the-badge&logo=postgresql&logoColor=white) ![Redis](https://img.shields.io/badge/redis-%23DD0031.svg?style=for-the-badge&logo=redis&logoColor=white)

## 검색
![Elasticsearch](https://img.shields.io/badge/elasticsearch-%230377CC.svg?style=for-the-badge&logo=elasticsearch&logoColor=white)

## 인프라
![Nginx](https://img.shields.io/badge/nginx-%23009639.svg?style=for-the-badge&logo=nginx&logoColor=white) ![Docker](https://img.shields.io/badge/docker-%230db7ed.svg?style=for-the-badge&logo=docker&logoColor=white)   ![AWS](https://img.shields.io/badge/AWS-%23FF9900.svg?style=for-the-badge&logo=amazon-aws&logoColor=white) 

## CI/CD
![GithubActions](https://img.shields.io/badge/github%20actions-%232671E5.svg?style=for-the-badge&logo=githubactions&logoColor=white)

# 주요 실행화면
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

