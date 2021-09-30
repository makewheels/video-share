package com.github.makewheels.videoshare.apigateway;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient("universal-user-service")
public interface UserService {
    @PostMapping("authLoginToken")
    Boolean authLoginToken(@RequestParam String loginToken);
}
