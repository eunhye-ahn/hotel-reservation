ALTER TABLE users ADD CONSTRAINT UK_user_email UNIQUE (email);

ALTER TABLE room_type
    ADD CONSTRAINT UK_room_type_hotel_name UNIQUE (hotel_id, name);
CREATE INDEX IDX_room_type_hotel_id ON room_type (hotel_id);

ALTER TABLE room_type_inventory
    ADD CONSTRAINT UK_inventory_room_type_date UNIQUE (room_type_id, date),
    ADD CONSTRAINT CHK_reserved_min CHECK (total_reserved >= 0),
    ADD CONSTRAINT CHK_reserved_max CHECK (total_reserved <= total_inventory * 1.1);
CREATE INDEX IDX_inventory_date ON room_type_inventory (date);

ALTER TABLE rate
    ADD CONSTRAINT UK_rate_room_type_date UNIQUE (room_type_id, date),
    ADD CONSTRAINT CHK_base_rate CHECK (base_rate > 0),
    ADD CONSTRAINT CHK_demand_rate_min CHECK (demand_rate >= base_rate),
    ADD CONSTRAINT CHK_demand_rate_max CHECK (demand_rate <= max_rate);
CREATE INDEX IDX_rate_date ON rate (date);

ALTER TABLE room
    ADD CONSTRAINT CHK_room_identifier CHECK (number IS NOT NULL OR name IS NOT NULL),
    ADD CONSTRAINT CHK_floor CHECK (floor > 0),
    ADD CONSTRAINT CHK_number CHECK (number > 0);
CREATE UNIQUE INDEX UK_room_hotel_number ON room (hotel_id, number) WHERE number IS NOT NULL;
CREATE UNIQUE INDEX UK_room_hotel_name ON room (hotel_id, name) WHERE name IS NOT NULL;
CREATE INDEX IDX_room_usable ON room (is_usable);

ALTER TABLE reservation
    ADD CONSTRAINT CHK_date_range CHECK (start_date < end_date),
    ADD CONSTRAINT CHK_total_price CHECK (total_price > 0),
    ADD CONSTRAINT CHK_number_of_rooms CHECK (number_of_rooms > 0);
CREATE INDEX IDX_reservation_user_id ON reservation (user_id);
CREATE INDEX IDX_reservation_status ON reservation (reservation_status);
CREATE INDEX IDX_payment_status ON reservation (payment_status);

CREATE INDEX IDX_payment_order_checkout_id ON payment_order (checkout_id);
CREATE INDEX IDX_payment_order_status ON payment_order (payment_order_status);

ALTER TABLE wallet
    ADD CONSTRAINT CHK_wallet_balance CHECK (balance >= 0);

ALTER TABLE ledger
    ADD CONSTRAINT CHK_ledger_debit_credit CHECK (debit IS NOT NULL OR credit IS NOT NULL),
    ADD CONSTRAINT CHK_debit_positive CHECK (debit IS NULL OR debit > 0),
    ADD CONSTRAINT CHK_credit_positive CHECK (credit IS NULL OR credit > 0);

ALTER TABLE settlement
    ADD CONSTRAINT CHK_settlement_amount CHECK (amount > 0),
    ADD CONSTRAINT CHK_settlement_period CHECK (period_start_date < period_end_date);
CREATE INDEX IDX_settlement_seller_account ON settlement (seller_account);
CREATE INDEX IDX_settlement_status ON settlement (status);
CREATE INDEX IDX_settlement_period ON settlement (period_start_date, period_end_date);

CREATE INDEX IDX_wish_collection_user_id ON wish_collection (user_id);
ALTER TABLE wish_collection
    ADD CONSTRAINT UK_wish_collection_user_name UNIQUE (user_id, name);

CREATE INDEX IDX_wish_list_hotel_id ON wish_list (hotel_id);