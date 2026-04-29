INSERT INTO users (name, email, password, phone, role, created_at, updated_at)
VALUES
    ('Test User', 'test@test.com', '$2a$10$5.OW9/S3MLMEmhTtIXp5s.pYRyOQ77zhOqDH5U36dIPsMH.qjyjaq', '01012345678', 'ROLE_USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
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
    (1, 1, 100000, 200000, 150000, CURRENT_DATE),
    (1, 2, 150000, 300000, 200000, CURRENT_DATE),
    (2, 3, 120000, 250000, 180000, CURRENT_DATE),
    (3, 4, 200000, 400000, 250000, CURRENT_DATE);


INSERT INTO room_type_inventory (room_type_id, hotel_id, date, total_inventory, total_reserved)
VALUES
    (1, 1, CURRENT_DATE, 10, 3),
    (2, 1, CURRENT_DATE, 8, 5),
    (3, 2, CURRENT_DATE, 12, 2),
    (4, 3, CURRENT_DATE, 5, 1);