package com.hotel.reservation.util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.reservation.domain.Hotel;
import com.hotel.reservation.repository.HotelRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.io.File;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

@Component
@Slf4j
public class DataLoader implements CommandLineRunner {

    @Autowired
    private HotelRepository hotelRepository;
    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public void run(String... args) throws Exception {
        if (hotelRepository.count() > 0) {
            log.info("already data load");
            return;
        }

        File file = new ClassPathResource("hotels.json").getFile();
        JsonNode root = objectMapper.readTree(file);
        JsonNode items = root.path("response").path("body").path("items").path("item");

        List<Hotel> hotels = new ArrayList<>();
        Random random = new Random();

        for (JsonNode item : items) {
            String randomSellerAccount = String.format("%03d-%04d-%06d",
                    random.nextInt(900) + 100,
                    random.nextInt(9000) + 1000,
                    random.nextInt(900000) + 100000);
            String firstImage = item.path("firstimage").asText();
            hotels.add(Hotel.builder()
                    .sellerAccount(randomSellerAccount)
                    .name(item.path("title").asText())
                    .address(item.path("addr1").asText())
                    .latitude(item.path("mapy").asDouble())
                    .longitude(item.path("mapx").asDouble())
                    .imageUrl(firstImage.isEmpty() ? "https://images.unsplash.com/photo-1566073771259-6a8506099945?w=800" : firstImage)
                    .checkInTime(LocalTime.of(14, 0))
                    .checkOutTime(LocalTime.of(11, 0))
                    .build());
        }

        hotelRepository.saveAll(hotels);
        log.info("저장 완료: {}건", hotels.size());
    }
}