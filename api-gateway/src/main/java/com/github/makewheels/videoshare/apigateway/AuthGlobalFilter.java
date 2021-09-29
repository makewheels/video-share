package com.github.makewheels.videoshare.apigateway;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.List;

@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        List<String> headers = exchange.getRequest().getHeaders().get("loginToken");
        if (CollectionUtils.isEmpty(headers)){

        }
        String loginToken = headers.get(0);

        return chain.filter(exchange);
    }

    @Override
    public int getOrder() {
        return 0;
    }
}
