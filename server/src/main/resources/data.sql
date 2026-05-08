INSERT INTO users (name, email, password, phone, role, created_at, updated_at)
VALUES
    ('Test User', 'test@test.com', '$2a$10$5.OW9/S3MLMEmhTtIXp5s.pYRyOQ77zhOqDH5U36dIPsMH.qjyjaq', '01012345678', 'ROLE_GUEST', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('Admin User', 'admin@test.com', '$2a$10$5.OW9/S3MLMEmhTtIXp5s.pYRyOQ77zhOqDH5U36dIPsMH.qjyjaq', '01099999999', 'ROLE_ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO hotel (name, address, latitude, longitude, image_url, check_in_time, check_out_time, created_at, updated_at)
VALUES
    ('롯데호텔 서울', '서울 중구 을지로 30', 37.5665, 126.9780, null, '14:00:00', '11:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('신라호텔', '서울 중구 장충동', 37.5512, 126.9882, null, '15:00:00', '11:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    ('제주 신화월드', '제주 서귀포시 안덕면', 33.3617, 126.3016, null, '14:00:00', '12:00:00', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO room_type (hotel_id, name, max_occupancy, image_url, created_at, updated_at)
VALUES
    (1, '스탠다드', 2, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (1, '디럭스', 3, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (2, '스탠다드', 2, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
    (3, '스위트', 4, null, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

INSERT INTO rate (hotel_id, room_type_id, base_rate, max_rate, demand_rate, date)
VALUES
    -- 롯데호텔 스탠다드 (room_type_id: 1)
    (1, 1, 100000, 200000, 150000, CURRENT_DATE),
    (1, 1, 100000, 200000, 150000, CURRENT_DATE + 1),
    (1, 1, 100000, 200000, 150000, CURRENT_DATE + 2),
    (1, 1, 100000, 200000, 150000, CURRENT_DATE + 3),
    (1, 1, 100000, 200000, 150000, CURRENT_DATE + 4),
    (1, 1, 100000, 200000, 150000, CURRENT_DATE + 5),
    (1, 1, 100000, 200000, 150000, CURRENT_DATE + 6),

    -- 롯데호텔 디럭스 (room_type_id: 2)
    (1, 2, 150000, 300000, 200000, CURRENT_DATE),
    (1, 2, 150000, 300000, 200000, CURRENT_DATE + 1),
    (1, 2, 150000, 300000, 200000, CURRENT_DATE + 2),
    (1, 2, 150000, 300000, 200000, CURRENT_DATE + 3),
    (1, 2, 150000, 300000, 200000, CURRENT_DATE + 4),
    (1, 2, 150000, 300000, 200000, CURRENT_DATE + 5),
    (1, 2, 150000, 300000, 200000, CURRENT_DATE + 6),

    -- 신라호텔 스탠다드 (room_type_id: 3)
    (2, 3, 120000, 250000, 180000, CURRENT_DATE),
    (2, 3, 120000, 250000, 180000, CURRENT_DATE + 1),
    (2, 3, 120000, 250000, 180000, CURRENT_DATE + 2),
    (2, 3, 120000, 250000, 180000, CURRENT_DATE + 3),
    (2, 3, 120000, 250000, 180000, CURRENT_DATE + 4),
    (2, 3, 120000, 250000, 180000, CURRENT_DATE + 5),
    (2, 3, 120000, 250000, 180000, CURRENT_DATE + 6),

    -- 제주 신화월드 스위트 (room_type_id: 4)
    (3, 4, 200000, 400000, 250000, CURRENT_DATE),
    (3, 4, 200000, 400000, 250000, CURRENT_DATE + 1),
    (3, 4, 200000, 400000, 250000, CURRENT_DATE + 2),
    (3, 4, 200000, 400000, 250000, CURRENT_DATE + 3),
    (3, 4, 200000, 400000, 250000, CURRENT_DATE + 4),
    (3, 4, 200000, 400000, 250000, CURRENT_DATE + 5),
    (3, 4, 200000, 400000, 250000, CURRENT_DATE + 6);

INSERT INTO room_type_inventory (room_type_id, hotel_id, date, total_inventory, total_reserved, version)
VALUES
    -- 롯데호텔 스탠다드 (room_type_id: 1)
    (1, 1, CURRENT_DATE,     10, 3, 0),
    (1, 1, CURRENT_DATE + 1, 10, 0, 0),
    (1, 1, CURRENT_DATE + 2, 10, 0, 0),
    (1, 1, CURRENT_DATE + 3, 10, 0, 0),
    (1, 1, CURRENT_DATE + 4, 10, 0, 0),
    (1, 1, CURRENT_DATE + 5, 10, 0, 0),
    (1, 1, CURRENT_DATE + 6, 10, 0, 0),

    -- 롯데호텔 디럭스 (room_type_id: 2)
    (2, 1, CURRENT_DATE,     10, 11, 0),
    (2, 1, CURRENT_DATE + 1, 8, 5, 0),
    (2, 1, CURRENT_DATE + 2, 8, 0, 0),
    (2, 1, CURRENT_DATE + 3, 8, 0, 0),
    (2, 1, CURRENT_DATE + 4, 8, 0, 0),
    (2, 1, CURRENT_DATE + 5, 8, 0, 0),
    (2, 1, CURRENT_DATE + 6, 8, 0, 0),

    -- 신라호텔 스탠다드 (room_type_id: 3)
    (3, 2, CURRENT_DATE,     100, 0, 0),
    (3, 2, CURRENT_DATE + 1, 12, 2, 0),
    (3, 2, CURRENT_DATE + 2, 12, 0, 0),
    (3, 2, CURRENT_DATE + 3, 12, 0, 0),
    (3, 2, CURRENT_DATE + 4, 12, 0, 0),
    (3, 2, CURRENT_DATE + 5, 12, 0, 0),
    (3, 2, CURRENT_DATE + 6, 12, 0, 0),

    -- 제주 신화월드 스위트 (room_type_id: 4)
    (4, 3, CURRENT_DATE,     5, 4, 0),
    (4, 3, CURRENT_DATE + 1, 5, 1, 0),
    (4, 3, CURRENT_DATE + 2, 5, 0, 0),
    (4, 3, CURRENT_DATE + 3, 5, 0, 0),
    (4, 3, CURRENT_DATE + 4, 5, 0, 0),
    (4, 3, CURRENT_DATE + 5, 5, 0, 0),
    (4, 3, CURRENT_DATE + 6, 5, 0, 0);