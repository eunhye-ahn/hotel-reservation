-- ---------- users ----------
CREATE TABLE users (
                       id              BIGSERIAL PRIMARY KEY,
                       name            VARCHAR(255) NOT NULL,
                       email           VARCHAR(255) NOT NULL,
                       password        VARCHAR(255),
                       phone           VARCHAR(50),
                       role            VARCHAR(30) NOT NULL DEFAULT 'ROLE_GUEST',
                       provider        VARCHAR(30),
                       provider_id     VARCHAR(255),
                       created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE UNIQUE INDEX uq_users_email ON users(email);

-- ---------- hotel ----------
CREATE TABLE hotel (
                       id                  BIGSERIAL PRIMARY KEY,
                       content_id          BIGINT,
                       name                VARCHAR(255) NOT NULL,
                       address             VARCHAR(255) NOT NULL,
                       seller_account      VARCHAR(255) NOT NULL UNIQUE,
                       latitude            DOUBLE PRECISION,
                       longitude           DOUBLE PRECISION,
                       image_url           VARCHAR(1000),
                       check_in_time       TIME NOT NULL,
                       check_out_time      TIME NOT NULL,
                       l_dong_regn_cd      VARCHAR(20),
                       l_dong_signgu_cd    VARCHAR(20),
                       lclsSystm2          VARCHAR(20),
                       created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------- room_type ----------
CREATE TABLE room_type (
                           id              BIGSERIAL PRIMARY KEY,
                           hotel_id        BIGINT NOT NULL REFERENCES hotel(id),
                           name            VARCHAR(255) NOT NULL,
                           max_occupancy   INT NOT NULL,
                           image_url       VARCHAR(1000),
                           created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------- room ----------
CREATE TABLE room (
                      id              BIGSERIAL PRIMARY KEY,
                      floor           INT NOT NULL,
                      number          INT,
                      name            VARCHAR(255),
                      is_usable       BOOLEAN NOT NULL DEFAULT TRUE,
                      hotel_id        BIGINT NOT NULL REFERENCES hotel(id),
                      room_type_id    BIGINT NOT NULL REFERENCES room_type(id),
                      created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------- rate ----------
CREATE TABLE rate (
                      id              BIGSERIAL PRIMARY KEY,
                      base_rate       INT NOT NULL,
                      max_rate        INT NOT NULL,
                      demand_rate     INT NOT NULL,
                      date            DATE NOT NULL,
                      hotel_id        BIGINT NOT NULL REFERENCES hotel(id),
                      room_type_id    BIGINT NOT NULL REFERENCES room_type(id),
                      created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------- room_type_inventory ----------
CREATE TABLE room_type_inventory (
                                     id              BIGSERIAL PRIMARY KEY,
                                     version         BIGINT NOT NULL DEFAULT 0,
                                     room_type_id    BIGINT REFERENCES room_type(id),
                                     date            DATE NOT NULL,
                                     hotel_id        BIGINT NOT NULL REFERENCES hotel(id),
                                     total_reserved  INT NOT NULL,
                                     total_inventory INT NOT NULL
);

-- ---------- reservation ----------
CREATE TABLE reservation (
                             id                      BIGSERIAL PRIMARY KEY,
                             display_reservation_no  VARCHAR(255) UNIQUE,
                             order_id                VARCHAR(255) NOT NULL,
                             reservation_key         VARCHAR(255) UNIQUE,
                             start_date              DATE NOT NULL,
                             end_date                DATE NOT NULL,
                             payment_status          VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                             reservation_status      VARCHAR(30) NOT NULL,
                             total_price             INT NOT NULL,
                             number_of_rooms         INT NOT NULL,
                             number_of_guests        INT NOT NULL,
                             room_type_id            BIGINT REFERENCES room_type(id),
                             room_id                 BIGINT REFERENCES room(id),
                             hotel_id                BIGINT NOT NULL REFERENCES hotel(id),
                             user_id                 BIGINT NOT NULL REFERENCES users(id),
                             cancel_type             VARCHAR(30),
                             cancel_reason           VARCHAR(1000),
                             created_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             updated_at              TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                             CONSTRAINT chk_reservation_date_range CHECK (start_date < end_date)
);

-- ---------- wallet ----------
CREATE TABLE wallet (
                        id              BIGSERIAL PRIMARY KEY,
                        version         BIGINT NOT NULL DEFAULT 0,
                        seller_account  VARCHAR(255) NOT NULL UNIQUE,
                        balance         INT NOT NULL DEFAULT 0,
                        created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------- ledger ----------
CREATE TABLE ledger (
                        id                  BIGSERIAL PRIMARY KEY,
                        payment_order_id    VARCHAR(255) NOT NULL,
                        account             VARCHAR(255) NOT NULL,
                        account_type        VARCHAR(30),
                        debit               INT,
                        credit              INT,
                        created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                        updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);


-- ---------- payment_event ----------
CREATE TABLE payment_event (
                               checkout_id         VARCHAR(255) PRIMARY KEY,
                               reservation_id      BIGINT NOT NULL UNIQUE,
                               order_id            VARCHAR(255) NOT NULL UNIQUE,
                               user_id             BIGINT NOT NULL,
                               reservation_key     VARCHAR(255) NOT NULL,
                               psp_type            VARCHAR(30) NOT NULL,
                               psp_token           VARCHAR(500),
                               created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------- payment_order ----------
CREATE TABLE payment_order (
                               payment_order_id        VARCHAR(255) PRIMARY KEY,
                               display_order_no        VARCHAR(255) UNIQUE,
                               checkout_id             VARCHAR(255) NOT NULL,
                               seller_account           VARCHAR(255) NOT NULL,
                               amount                   INT,
                               payment_order_status     VARCHAR(30) NOT NULL DEFAULT 'NOT_STARTED',
                               ledger_updated           BOOLEAN NOT NULL DEFAULT FALSE,
                               wallet_updated           BOOLEAN NOT NULL DEFAULT FALSE,
                               retry_count              INT NOT NULL DEFAULT 0,
                               created_at               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                               updated_at               TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------- settlement ----------
CREATE TABLE settlement (
                            id                  BIGSERIAL PRIMARY KEY,
                            settlement_key      VARCHAR(255) NOT NULL UNIQUE,
                            seller_account      VARCHAR(255) NOT NULL,
                            amount              INT NOT NULL,
                            period_start_date   DATE NOT NULL,
                            period_end_date     DATE NOT NULL,
                            settled_at          TIMESTAMP,
                            status              VARCHAR(30) NOT NULL DEFAULT 'PENDING',
                            created_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                            updated_at          TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- ---------- wish_collection ----------
CREATE TABLE wish_collection (
                                 id          BIGSERIAL PRIMARY KEY,
                                 name        VARCHAR(255) NOT NULL,
                                 user_id     BIGINT NOT NULL REFERENCES users(id),
                                 created_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                 updated_at  TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE wish_list (
                           id              BIGSERIAL PRIMARY KEY,
                           collection_id   BIGINT REFERENCES wish_collection(id),
                           hotel_id        BIGINT REFERENCES hotel(id),
                           created_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           updated_at      TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           CONSTRAINT uq_wish_list_collection_hotel UNIQUE (collection_id, hotel_id)
);

