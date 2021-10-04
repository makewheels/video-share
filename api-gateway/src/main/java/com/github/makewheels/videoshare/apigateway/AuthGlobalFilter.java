package com.github.makewheels.videoshare.apigateway;

import com.google.common.collect.Lists;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class AuthGlobalFilter implements GlobalFilter, Ordered {

    @Resource
    private UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();
        HttpHeaders headers = request.getHeaders();

        String path = request.getPath().value();
        log.info("path = " + path);
        //放行列表
        ArrayList<String> list = Lists.newArrayList(
                "/universal-user-service/user/login",
                "/video-service/video/getVideoInfoByVideoId",
                "/video-service/video/getPlayUrl"
        );
        if (StringUtils.isNotEmpty(path) && list.contains(path)) {
            return chain.filter(exchange);
        }

        //调用通用用户微服务，校验loginToken
        List<String> loginTokenHeaders = headers.get("loginToken");
        if (CollectionUtils.isEmpty(loginTokenHeaders)) {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
        //如果校验通过则放行，如果校验失败则拦截，返回401状态码
        String loginToken = loginTokenHeaders.get(0);
        if (userService.authLoginToken(loginToken)) {
            return chain.filter(exchange);
        } else {
            response.setStatusCode(HttpStatus.UNAUTHORIZED);
            return response.setComplete();
        }
    }

    @Override
    public int getOrder() {
        return 0;
    }


}
