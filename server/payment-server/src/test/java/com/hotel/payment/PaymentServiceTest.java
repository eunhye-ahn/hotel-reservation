package com.hotel.payment;

import com.hotel.payment.client.ReservationClient;
import com.hotel.payment.dto.PaymentPrepareResponse;
import com.hotel.payment.dto.ReservationFeignResponse;
import com.hotel.payment.repository.PaymentEventRepository;
import com.hotel.payment.repository.PaymentOrderRepository;
import com.hotel.payment.service.PaymentService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;
import static org.assertj.core.api.Assertions.assertThat;


@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class PaymentServiceTest {
    @Mock
    private ReservationClient reservationClient;

    @Mock
    private PaymentEventRepository paymentEventRepository;

    @Mock
    private PaymentOrderRepository paymentOrderRepository;

    @InjectMocks
    private PaymentService paymentService;

    private ReservationFeignResponse mockReservation(){
        return ReservationFeignResponse.builder()
                .reservationId(1L)
                .reservationKey("RSV-001")
                .sellerAccount("110-123-456789")
                .paymentStatus("PENDING")
                .amount(100000)
                .build();
    }

    @Test
    void 결제준비_성공(){
        //given: 테스트환경 세팅
        when(reservationClient.getReservationForPayment(mockReservation().getReservationKey()))
                .thenReturn(mockReservation());

        //when: 실제 테스트할 메서드 실행
        PaymentPrepareResponse response = paymentService.preparePayment(mockReservation().getReservationKey());

        //then: 결과검증(assertThat, verify)
        assertThat(response.getPaymentOrderId()).isNotNull();
        assertThat(response.getAmount()).isEqualTo(100000);
    }

    @Test
    void 결제준비_실패_PENDING아닐때(){
        //given: 테스트환경 세팅
        ReservationFeignResponse notPending = ReservationFeignResponse.builder()
                    .reservationId(1L)
                    .reservationKey("RSV-001")
                    .sellerAccount("110-123-456789")
                    .paymentStatus("COMPLETED")
                    .amount(100000)
                    .build();
        when(reservationClient.getReservationForPayment(notPending.getReservationKey()))
                .thenReturn(notPending);

        // when & then
        assertThatThrownBy(() -> paymentService.preparePayment(notPending.getReservationKey()))
                .isInstanceOf(IllegalStateException.class)
                .hasMessage("결제 가능한 예약이 아닙니다.");
    }
}
