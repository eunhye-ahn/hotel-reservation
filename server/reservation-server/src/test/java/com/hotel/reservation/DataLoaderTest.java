package com.hotel.reservation;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.reservation.domain.Hotel;
import com.hotel.reservation.repository.HotelRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.test.context.ActiveProfiles;

import java.io.File;
import java.io.IOException;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest         //spring 컨텍스트 로드
@Slf4j
@ActiveProfiles("prod")
public class DataLoaderTest {
    @Autowired
    private HotelRepository hotelRepository;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void generateFromJson() throws IOException {
        File file = new ClassPathResource("hotels.json").getFile();
        JsonNode root = objectMapper.readTree(file);
        JsonNode items = root.path("response").path("body").path("items").path("item");

        List<Hotel> hotels = new ArrayList<>();
        for (JsonNode item : items) {
            String firstImage = item.path("firstimage").asText();
            hotels.add(Hotel.builder()
                    .name(item.path("title").asText())
                    .address(item.path("addr1").asText())
                    .latitude(item.path("mapy").asDouble())
                    .longitude(item.path("mapx").asDouble())
                    .imageUrl(firstImage.isEmpty() ? null : firstImage)
                    .checkInTime(LocalTime.of(14, 0))
                    .checkOutTime(LocalTime.of(11, 0))
                    .build());
        }

        hotelRepository.saveAll(hotels);
        log.info("저장 완료: {}건", hotels.size());
    }
}