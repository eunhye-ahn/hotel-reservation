package com.hotel.reservation;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ReservationApplicationTests {

	/**
	 * 1. @SpringBootTest가 Spring 컨텍스트 띄우기 시작
	 * 2. 모든 @Bean, @Service, @Repository 등 등록 시도
	 * 3. 성공 → contextLoads() 실행 → 비어있으니까 그냥 통과 → 테스트 성공
	 *    실패 → 컨텍스트 못 뜸 → 테스트 실패
	 *
	 *
	 * 아래와 같은 오류들
	 * No qualifying bean of type 'RedisTemplate<Object, Object>'
	 * → contextLoads() 실패
	 * → 빈설정이 잘못됨을 바로 확인 가능
	 */
	@Test
	void contextLoads() {
	}

}
