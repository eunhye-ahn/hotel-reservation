## 일일 정산 배치 흐름
```mermaid
sequenceDiagram
    participant Sch as SettlementScheduler
    participant SS as SettlementService
    participant STS as SettlementTransactionService
    participant RS as RedisService
    participant DB as Database

    rect rgb(255, 249, 196)
        Note over Sch, DB: 일일 정산 배치 실행 (Cron: 매일 03:00)
    end
    Sch->>SS: 정산 실행
    SS->>DB: 잔액 보유 지갑 목록 조회
    DB-->>SS: 대상 지갑 목록 반환

    loop 각 대상 지갑별 정산 처리
        SS->>DB: 정산 대상 금액 계산
        DB-->>SS: 정산 금액 반환

        SS->>STS: 정산 실행 메서드 호출
        STS->>DB: 정산 저장 (PENDING 상태)
        DB-->>STS: 저장 완료
        Note over STS: 트랜잭션 커밋
        STS-->>SS: settlementId 반환

        alt 정산 처리 성공
            SS->>STS: 정산 성공 메서드 호출
            STS->>DB: 정산 상태 변경 (PENDING -> COMPLETED)
            STS->>DB: 지갑 잔액 차감
            DB-->>STS: 변경 완료
            Note over STS: 트랜잭션 커밋

        else 정산 처리 실패
            SS->>STS: 정산 실패 메서드 호출
            STS->>DB: 정산 상태 변경 (PENDING -> FAILED)
            DB-->>STS: 변경 완료
            Note over STS: 트랜잭션 커밋
        end
    end

    SS-->>Sch: 배치 완료
```

## 정산 수동실행 -관리자
```mermaid
sequenceDiagram
    participant Client as Client
    participant ASC as AdminSettlementController
    participant ASS as AdminSettlementService
    participant SS as SettlementService
    participant STS as SettlementTransactionService
    participant RS as RedisService
    participant DB as Database

    rect rgb(255, 249, 196)
        Note over Client, DB: Case 1: 정산 금액 조회 (Preview)
    end
    Client->>ASC: 정산 금액 조회 요청 (GET /{hotelId}/preview)
    ASC->>ASS: previewSettlementAmount() 호출
    ASS->>DB: 호텔 조회 및 장부 금액 계산
    DB-->>ASC: 정산 금액 반환
    ASC-->>Client: 200 OK (amount)

    rect rgb(255, 249, 196)
        Note over Client, DB: Case 2: 수동 정산 실행 (Execute)
    end
    Client->>ASC: 수동 정산 실행 요청 (POST /{hotelId}/execute)
    ASC->>ASS: 수동 정산 위임
    ASS->>SS: 정산 처리 위임
    SS->>DB: 호텔 및 지갑 정보 조회
    DB-->>SS: 정보 반환
    SS->>DB: 정산 대상 금액 계산
    DB-->>SS: 정산 금액 반환

    SS->>STS: createPendingSettlement() 호출
    STS->>DB: Settlement 저장 (PENDING 상태)
    DB-->>STS: 저장 완료
    Note over STS: 트랜잭션 커밋
    STS-->>SS: settlementId 반환

    alt 정산 처리 성공 (try)
        SS->>STS: 정산 성공 메서드 호출
        STS->>DB: Settlement 상태 변경 (PENDING -> COMPLETED)
        STS->>DB: 지갑 잔액 차감
        DB-->>STS: 변경 완료
        Note over STS: 트랜잭션 커밋

    else 정산 처리 실패 (catch)
        SS->>STS: 정산 실패 메서드 호출
        STS->>DB: Settlement 상태 변경 (PENDING -> FAILED)
        DB-->>STS: 변경 완료
        Note over STS: 트랜잭션 커밋
    end

    SS-->>ASC: 처리 완료
    ASC-->>Client: 200 OK
```

---
### 트랜잭션 분리 아키텍처

| 구분 | 주요 수행 작업 | 트랜잭션 / 데이터 동작 | 분리 사유 및 핵심 효과 |
| :--- | :--- | :--- | :--- |
| **TX 1**<br> (정산 내역 선점) | • 정산 시도 이력 생성 | **Settlement** 생성 (`PENDING`) | 정산 시작 상태를 독립적으로 먼저 DB에 기록하여 **작업 추적** 및 실패 시 변경할 `settlementId` 반환 |
| **TX 2**<br> (정산 완료 처리) | • 정산 상태 완료 변경<br>• 지갑 잔액 차감 | **Settlement** `PENDING`-> `COMPLETED` <br>DB: 지갑 잔액 차감 | 지갑 잔액 차감과 정산 상태 확정을 **단일 작업 단위로 처리** |
| **TX 3**<br> (정산 실패 처리) | • 정산 실패 상태 기록 | **Settlement** `PENDING` -> `FAILED`| 예외 발생 시 `PENDING` 건을 실패 상태로 저장하여 **정산 상태 불일치 방지 및 배치 재시도 대상 식별** |

