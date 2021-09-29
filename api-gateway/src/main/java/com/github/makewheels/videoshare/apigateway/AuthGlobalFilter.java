package com.github.makewheels.videoshare.apigateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
@Slf4j
public class AuthGlobalFilter implements GlobalFilter, Ordered {
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        System.out.println(System.currentTimeMillis());
        System.out.println(exchange.getRequest().getHeaders());
//        exchange.getResponse().setComplete()
        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
