package com.hotel.common.idempotency;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hotel.payment.domain.PaymentEvent;
import com.hotel.payment.domain.PaymentOrder;
import com.hotel.payment.domain.Settlement;
import com.hotel.payment.dto.PaymentPrepareResponse;
import com.hotel.payment.repository.PaymentEventRepository;
import com.hotel.payment.repository.PaymentOrderRepository;
import com.hotel.payment.repository.SettlementRepository;
import com.hotel.reservation.domain.Reservation;
import com.hotel.reservation.dto.ReservationCreateResponse;
import com.hotel.reservation.repository.ReservationRepository;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
@RequiredArgsConstructor
public class IdempotencyInterceptor implements HandlerInterceptor {
    private final PaymentEventRepository paymentEventRepository;
    private final ObjectMapper objectMapper;
    private final PaymentOrderRepository paymentOrderRepository;
    private final ReservationRepository reservationRepository;
    private final IdempotencyRedisService redisService;
    private final SettlementRepository settlementRepository;
    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception{

        String uri = request.getRequestURI();
        //우회-관리자취소
        if(uri.startsWith("/api/v1/admin/reservation")){
            String reservationId = extractReservationId(uri, response, RESERVATION_CANCEL_PATTERN);
            if(reservationId == null){
                return false;
            }
            String cancelKey = "cancel:admin:" + reservationId;
            return handleReservationCancelByAdmin(cancelKey, request, response);
        }
        //우회-사용자취소
        if("DELETE".equalsIgnoreCase(request.getMethod()) && uri.startsWith("/api/v1/reservations/")){
            String reservationKey = extractReservationId(uri, response, USER_RESERVATION_CANCEL_PATTERN);
            if(reservationKey == null){
                return false;
            }
            String cancelKey = "cancel:user:" + reservationKey;
            return handleReservationCancelByUser(cancelKey, request, response);
        }

        //Idempotency-Key 헤더 확인 (없으면 통과)
        String idempotencyKey = request.getHeader("Idempotency-Key");
        if(idempotencyKey == null){
            return true;
        }



        if (uri.startsWith("/api/v1/payments")) {
            return handlePayment(idempotencyKey, request, response);
        }
        if (uri.startsWith("/api/v1/reservations")) {
            return handleReservation(idempotencyKey, request, response);
        }
        if(uri.startsWith("/api/v1/admin/settlement")) {
            return handleSettlement(idempotencyKey, request, response);
        }

        //다른도메인이면 통과
        return true;
    }

    //결제 도메인 멱등성 체크 : 이미 처리된 checkout_id면 캐시된 응답 반환하고 컨트롤러 진입
    private boolean handlePayment(String checkoutId, HttpServletRequest request, HttpServletResponse response) throws Exception{
        Long userId = resolveUserId(request);
        String requestHash = null;
        //선점시도
        boolean acquired = redisService.tryProcessing(IdempotencyDomain.PAYMENT, checkoutId, userId, requestHash);
        if(acquired){
            return true;
        }

        //이미 선점된 상태 -> 기존 값 조회
        Optional<IdempotencyValue> existing = redisService.get(IdempotencyDomain.PAYMENT, checkoutId);
        String status = existing.map(IdempotencyValue::getStatus).orElse("processing");
        //processing => 동시 중복 요청 => 409
        if("processing".equals(status)){
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return false;
        }
        //failed => 재시도 허용
        if ("failed".equals(status)) {
            return true;
        }
        //completed => DB에서 실제 응답 조립해서 캐시 반환
        Optional<PaymentEvent> paymentEvent = paymentEventRepository.findById(checkoutId);
        if (paymentEvent.isEmpty()) {
            // Redis는 completed인데 DB엔 없는 이상 케이스 -> 방어적으로 통과
            return true;
        }

        PaymentEvent event = paymentEvent.get();
        PaymentOrder order = paymentOrderRepository.findByCheckoutId(event.getCheckoutId())
                .orElseThrow();
        PaymentPrepareResponse cachedResponse = new PaymentPrepareResponse(
                event.getCheckoutId(),
                order.getAmount(),
                event.getUserId()
        );

        writeCachedResponse(response, cachedResponse);
        //중복요청 => 컨트롤러 진입 차단
        return false;
    }

    //예약도메인 멱등성 체크
    private boolean handleReservation(String reservationKey, HttpServletRequest request, HttpServletResponse response) throws Exception{
        Long userId = resolveUserId(request);
        String requestHash = null;

        boolean acquired = redisService.tryProcessing(IdempotencyDomain.RESERVATION, reservationKey, userId, requestHash);
        //선점시도
        if (acquired) {
            return true;
        }
        //이미 선점된 상태 -> 기존 값 조회
        Optional<IdempotencyValue> existing = redisService.get(IdempotencyDomain.RESERVATION, reservationKey);
        String status = existing.map(IdempotencyValue::getStatus).orElse("processing");

        //processing -> 동시 중복요청 -> 409
        if ("processing".equals(status)) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return false;
        }
        // failed -> 재시도 허용
        if ("failed".equals(status)) {
            return true;
        }
        //completed -> DB에서 실제 응답 조립해서 캐시 반환
        Optional<Reservation> reservation = reservationRepository.findByReservationKey(reservationKey);
        if (reservation.isEmpty()) {
            // Redis는 completed인데 DB엔 없는 이상 케이스 -> 방어적으로 통과
            return true;
        }
        Reservation res = reservation.get();
        writeCachedResponse(response, new ReservationCreateResponse(
                res.getReservationKey(),
                res.getOrderId()
        ));

        return false;
    }

    private boolean handleSettlement(String settlementKey, HttpServletRequest request, HttpServletResponse response) throws Exception{
        Long userId = resolveUserId(request);
        String requestHash = null;

        boolean acquired = redisService.tryProcessing(IdempotencyDomain.SETTLEMENT, settlementKey, userId, requestHash);
        //선점시도
        if (acquired) {
            return true;
        }

        //이미 선점된 상태 -> 기존 값 조회
        Optional<IdempotencyValue> existing = redisService.get(IdempotencyDomain.SETTLEMENT, settlementKey);
        String status = existing.map(IdempotencyValue::getStatus).orElse("processing");

        if ("processing".equals(status)) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return false;
        }
        if("failed".equals(status)){
            return true;
        }
        Optional<Settlement> settlement = settlementRepository.findBySettlementKey(settlementKey);
        if (settlement.isEmpty()) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return false;
        }

        response.setStatus(HttpServletResponse.SC_OK);
        return false;
    }

    private boolean handleReservationCancelByAdmin(String cancelKey, HttpServletRequest request, HttpServletResponse response) throws Exception{
        Long userId = resolveUserId(request);
        String requestHash = null;

        //선점시도
        boolean acquired = redisService.tryProcessing(IdempotencyDomain.RESERVATION_CANCEL_ADMIN, cancelKey, userId, requestHash);
        if(acquired){
            return true;
        }
        //이미 선점된 상태 -> 기존 값 조회
        Optional<IdempotencyValue> existing = redisService.get(IdempotencyDomain.RESERVATION_CANCEL_ADMIN, cancelKey);
        String status = existing.map(IdempotencyValue::getStatus).orElse("processing");

        if ("processing".equals(status)) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return false;
        }
        if("failed".equals(status)){
            return true;
        }
        //db검사 생략
        response.setStatus(HttpServletResponse.SC_OK);
        return false;
    }

    private boolean handleReservationCancelByUser(String cancelKey, HttpServletRequest request, HttpServletResponse response){
        Long userId = resolveUserId(request);
        String requestHash = null;

        //선점시도
        boolean acquired = redisService.tryProcessing(IdempotencyDomain.RESERVATION_CANCEL_USER, cancelKey, userId, requestHash);
        if(acquired){
            return true;
        }
        //이미 선점된 상태 -> 기존 값 조회
        Optional<IdempotencyValue> existing = redisService.get(IdempotencyDomain.RESERVATION_CANCEL_USER, cancelKey);
        String status = existing.map(IdempotencyValue::getStatus).orElse("processing");

        if ("processing".equals(status)) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
            return false;
        }
        if("failed".equals(status)){
            return true;
        }
        //db검사 생략
        response.setStatus(HttpServletResponse.SC_OK);
        return false;
    }

    //캐시된 응담을 JSON으로 감싸주는 공통로직
    private void writeCachedResponse(HttpServletResponse response, Object body) throws Exception{
        response.setStatus(HttpServletResponse.SC_OK);
        response.setContentType("application/json;charset=UTF-8");
        response.getWriter().write(objectMapper.writeValueAsString(body));
    }

    private Long resolveUserId(HttpServletRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if(authentication==null || !authentication.isAuthenticated()){
            return null;
        }
        Object principal = authentication.getPrincipal();
        return (principal instanceof Long ? (Long) principal : null);
    }

    //예약취소용 - 예약ID 추출
    private static final Pattern RESERVATION_CANCEL_PATTERN =
            Pattern.compile("/api/v1/admin/reservation/(\\d+)/cancel");
    private static final Pattern USER_RESERVATION_CANCEL_PATTERN =
            Pattern.compile("/api/v1/reservations/([^/]+)");


    private String extractReservationId(String uri, HttpServletResponse response, Pattern pattern) {
        Matcher matcher = pattern.matcher(uri);
        if (matcher.matches()) {
            return matcher.group(1);
        }
        response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        return null;
    }


}
