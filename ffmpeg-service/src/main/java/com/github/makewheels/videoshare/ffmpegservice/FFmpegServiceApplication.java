package com.github.makewheels.videoshare.ffmpegservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@EnableFeignClients
@EnableDiscoveryClient
@ComponentScan(basePackages = {
        "com.github.makewheels.videoshare",
        "com.github.makewheels.universaluserservice"
})
@SpringBootApplication
public class FFmpegServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FFmpegServiceApplication.class, args);
    }

}
