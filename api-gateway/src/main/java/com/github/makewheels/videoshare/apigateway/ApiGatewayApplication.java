package com.github.makewheels.videoshare.apigateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

//@ComponentScan(basePackages = {
//        "com.github.makewheels.videoshare",
//        "com.github.makewheels.universaluserservice"
//})
@EnableDiscoveryClient
@SpringBootApplication()
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

}
