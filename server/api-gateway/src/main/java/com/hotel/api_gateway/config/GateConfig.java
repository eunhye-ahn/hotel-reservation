package com.hotel.api_gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.server.mvc.filter.HttpHeadersFilter;
import org.springframework.cloud.gateway.server.mvc.handler.GatewayRouterFunctions;
import org.springframework.cloud.gateway.server.mvc.handler.HandlerFunctions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.function.RequestPredicates;
import org.springframework.web.servlet.function.RouterFunction;
import org.springframework.web.servlet.function.ServerRequest;
import org.springframework.web.servlet.function.ServerResponse;

@Configuration
@Slf4j
public class GateConfig {

    @Bean
    public RouterFunction<ServerResponse> reservationRoutes() {
        return GatewayRouterFunctions.route("reservation-service")
                .route(RequestPredicates.path("/api/v1/reservations/**")
                                .or(RequestPredicates.path("/api/v1/auth/**")),
                        HandlerFunctions.http("http://localhost:8080"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> paymentRoutes() {
        return GatewayRouterFunctions.route("payment-service")
                .route(RequestPredicates.path("/api/v1/payments/**"),
                        HandlerFunctions.http("http://localhost:8081"))
                .build();
    }

    @Bean
    public RouterFunction<ServerResponse> hotelRoutes() {
        return GatewayRouterFunctions.route("hotel-service")
                .route(RequestPredicates.path("/api/v1/hotels/**"),
                        HandlerFunctions.http("http://localhost:8082"))
                .before(request -> {
                    String userId = (String) request.servletRequest().getAttribute("X-User-Id");
                    log.info("before filter userId: {}", userId);  // 추가
                    return ServerRequest.from(request)
                            .header("X-User-Id", userId != null ? userId : "")
                            .build();
                })
                .build();
    }
}