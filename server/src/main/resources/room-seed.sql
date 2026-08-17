-- ============================================
-- room-seed.sql
-- hotel 데이터가 저장된 이후 실행되어야 함 (DataLoader에서 hotelRepository.saveAll() 직후 호출)
-- room_type / room / rate / room_type_inventory 시드 데이터 생성
-- ============================================

WITH room_type_templates (name, max_occupancy, image_url) AS (
    VALUES
        ('스탠다드', 2, 'https://images.unsplash.com/photo-1618773928121-c32242e63f39?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8Mnx8aG90ZWwlMjByb29tfGVufDB8fDB8fHww'),
        ('디럭스', 3, 'https://plus.unsplash.com/premium_photo-1661962493427-910e3333cf5a?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8M3x8c3RhbmRhcmQlMjByb29tfGVufDB8fDB8fHww'),
        ('스위트', 4, 'https://images.unsplash.com/photo-1562438668-bcf0ca6578f0?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8N3x8bHV4dXJ5JTIwcm9vbXxlbnwwfHwwfHx8MA%3D%3D'),
        ('이그제큐티브', 3, 'https://plus.unsplash.com/premium_photo-1661963239507-7bdf41a5e66b?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8OXx8bHV4dXJ5JTIwcm9vbXxlbnwwfHwwfHx8MA%3D%3D'),
        ('프리미어', 4, 'https://images.unsplash.com/photo-1738168246881-40f35f8aba0a?w=500&auto=format&fit=crop&q=60&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxzZWFyY2h8MTh8fGx1eHVyeSUyMHJvb218ZW58MHx8MHx8fDA%3D'),
        ('트윈룸', 2, 'https://images.unsplash.com/photo-1611892440504-42a792e24d32?w=500&auto=format&fit=crop&q=60'),
        ('더블룸', 2, 'https://images.unsplash.com/photo-1590490360182-c33d57733427?w=500&auto=format&fit=crop&q=60'),
        ('트리플룸', 3, 'https://images.unsplash.com/photo-1595576508898-0ad5c879a061?w=500&auto=format&fit=crop&q=60'),
        ('패밀리룸', 5, 'https://images.unsplash.com/photo-1631049307264-da0ec9d70304?w=500&auto=format&fit=crop&q=60'),
        ('주니어 스위트', 3, 'https://images.unsplash.com/photo-1771775529138-a7a20ba7e032?q=80&w=1169&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D'),
        ('로열 스위트', 4, 'https://images.unsplash.com/photo-1631049421450-348ccd7f8949?q=80&w=1170&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D'),
        ('오션뷰 디럭스', 3, 'https://images.unsplash.com/photo-1582719508461-905c673771fd?w=500&auto=format&fit=crop&q=60'),
        ('시티뷰 스탠다드', 2, 'https://images.unsplash.com/photo-1618221469555-7f3ad97540d6?w=500&auto=format&fit=crop&q=60'),
        ('펜트하우스', 4, 'https://images.unsplash.com/photo-1600585154340-be6161a56a0c?w=500&auto=format&fit=crop&q=60'),
        ('비즈니스룸', 2, 'https://images.unsplash.com/photo-1611048267451-e6ed903d4a38?w=500&auto=format&fit=crop&q=60')
),
     hotel_room_counts AS (
         SELECT id AS hotel_id, (3 + (RANDOM() * 2)::INT) AS room_type_count  -- 3~5개
         FROM hotel
     )
INSERT INTO room_type (hotel_id, name, max_occupancy, image_url, created_at, updated_at)
SELECT
    hrc.hotel_id,
    rt.name,
    rt.max_occupancy,
    rt.image_url,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM hotel_room_counts hrc
         CROSS JOIN LATERAL (
    SELECT name, max_occupancy, image_url
    FROM room_type_templates
    ORDER BY RANDOM()
        LIMIT hrc.room_type_count
) rt;

INSERT INTO room (floor, number, is_usable, hotel_id, room_type_id, created_at, updated_at)
SELECT
    f.floor_no,
    f.floor_no * 100 + f.seq AS number,   -- 예: 3층 2번째 방 -> 302
    CASE WHEN RANDOM() < 0.05 THEN false ELSE true END,
    rt.hotel_id,
    rt.id,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM room_type rt
         CROSS JOIN LATERAL (
    SELECT
        (2 + ((row_number() OVER () - 1) / 5))::INT AS floor_no,  -- 한 층에 5개씩 배정
        ((row_number() OVER () - 1) % 5) + 1 AS seq
FROM generate_series(1, (3 + (RANDOM() * 7)::INT))
    ) f;

INSERT INTO room_type_inventory (room_type_id, hotel_id, date, total_inventory, total_reserved, version)
SELECT
    rt.id,
    rt.hotel_id,
    CURRENT_DATE + (s * INTERVAL '1 day'),
    rc.usable_count,
    0,
    0
FROM room_type rt
         JOIN (
    SELECT room_type_id, COUNT(*) AS usable_count
    FROM room
    WHERE is_usable = true
    GROUP BY room_type_id
) rc ON rc.room_type_id = rt.id
         CROSS JOIN generate_series(0, 29) AS s;

INSERT INTO rate (room_type_id, hotel_id, base_rate, max_rate, demand_rate, date, created_at, updated_at)
SELECT
    base.id,
    base.hotel_id,
    base.base_rate,
    (base.base_rate * (1.5 + RANDOM() * 0.5))::INT / 1000 * 1000 AS max_rate,
    (base.base_rate * (1.1 + RANDOM() * 0.3))::INT / 1000 * 1000 AS demand_rate,
            CURRENT_DATE + s,
    CURRENT_TIMESTAMP,
    CURRENT_TIMESTAMP
FROM (
         SELECT
             rt.id,
             rt.hotel_id,
             CASE
                 WHEN rt.max_occupancy <= 2 THEN (80000 + (RANDOM() * 40000)::INT / 1000 * 1000)
                 WHEN rt.max_occupancy = 3 THEN (130000 + (RANDOM() * 70000)::INT / 1000 * 1000)
                 ELSE (250000 + (RANDOM() * 150000)::INT / 1000 * 1000)
                 END AS base_rate
         FROM room_type rt
     ) base
         CROSS JOIN generate_series(0, 29) AS s;