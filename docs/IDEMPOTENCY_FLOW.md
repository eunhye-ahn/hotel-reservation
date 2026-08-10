## Redis 멱등키 TTL 전략

| 도메인 | 상태 (Status) | TTL | 비고 / 설정 사유 |
| :--- | :--- | :--- | :--- |
| **예약** | `processing` | 1분 | 동시 요청 선점 및 처리 대기 |
| | `completed` | 10분 | 임시 예약 유효 기간(10분)과 동기화 |
| | `failed` | 1분 | 실패 건에 대한 빠른 재시도 허용 |
| **결제** | `processing` | 1분 | 외부 PG 통신 대기 |
| | `completed` | 30분 | 결제 승인 응답 재사용 및 결과 보존 |
| | `failed` | 1분 | 실패 건에 대한 빠른 재시도 허용 |
| **정산 수동 처리** | `processing` | 1분 | 수동 정산 중복 실행 방지 |
| | `completed` | 1일 | 당일 중복 정산 요청 원천 차단 |
| | `failed` | 1분 | 실패 건에 대한 빠른 재시도 허용 |

## 멱등키 흐름
```mermaid
sequenceDiagram
    participant Client as Client
    participant IT as IdempotencyInterceptor
    participant IS as IdempotencyRedisService
    participant C as Controller

    rect rgb(255, 249, 196)
        Note over Client, C: Case 1: 최초 요청 (정상 진입)
    end
    Client->>IT: 요청 전송 (Header: Idempotency-Key)
    IT->>IS: tryProcessing(domain, key, userId, hash)
    IS-->>IT: 선점 결과 반환 (true)
    
    IT->>C: 비즈니스 로직 실행
    C-->>IT: 응답 생성 (성공 또는 예외)
    
    IT->>IS: complete(key, response) (상태: COMPLETED)
    IS-->>IT: 저장 완료
    IT-->>Client: 최종 응답 반환

    rect rgb(255, 249, 196)
        Note over Client, C: Case 2: 재시도 요청 (응답 재사용 및 차단)
    end
    Client->>IT: 재요청 전송 (Header: Idempotency-Key)
    IT->>IS: 키 상태 조회 (GET Key)

    alt Status: PROCESSING (처리 중)
        IS-->>IT: Status: PROCESSING
        IT-->>Client: 409 Conflict (처리 중)

    else Status: COMPLETED (처리 완료)
        IS-->>IT: Status: COMPLETED + Cached Response
        IT-->>Client: 200 OK + 저장된 응답

    else Status: FAILED (처리 실패)
        IS-->>IT: Status: FAILED
        IT-->>Client: 500 Internal Server Error
    end
```

## 클래스 다이어그램

```mermaid
classDiagram
    class IdempotencyDomain {
        <<enumeration>>
        -Duration processingTtl
        -Duration completedTtl
        -Duration failedTtl
        +getTtlByStatus(String status) Duration
    }

    class IdempotencyValue {
        -String status
        -Long userId
        -String requestHash
        -LocalDateTime createdAt
    }

    class IdempotencyInterceptor {
        -PaymentEventRepository paymentEventRepository
        -PaymentOrderRepository paymentOrderRepository
        -ReservationRepository reservationRepository
        -SettlementRepository settlementRepository
        -IdempotencyRedisService redisService
        -ObjectMapper objectMapper
        +preHandle(request, response, handler) boolean
        -handlePayment(checkoutId, request, response) boolean
        -handleReservation(reservationKey, request, response) boolean
        -handleSettlement(settlementKey, request, response) boolean
        -writeCachedResponse(response, body) void
        -resolveUserId(request) Long
    }

    class IdempotencyRedisService {
        -RedisTemplate~String, Object~ objectRedisTemplate
        -ObjectMapper redisObjectMapper
        -String PREFIX
        -buildKey(IdempotencyDomain, String) String
        +tryProcessing(domain, key, userId, requestHash) boolean
        +get(domain, key) Optional~IdempotencyValue~
        +complete(domain, key) void
        +fail(domain, key) void
        -updateStatus(domain, key, newStatus) void
    }

    IdempotencyInterceptor --> IdempotencyRedisService : uses
    IdempotencyInterceptor --> IdempotencyDomain : uses
    IdempotencyRedisService --> IdempotencyDomain : uses
    IdempotencyRedisService ..> IdempotencyValue : manages
```
