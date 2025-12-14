package com.platform.server;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"com.platform.common", "com.platform.server"})
@ComponentScan(basePackages = {"com.platform.common", "com.platform.server"})
@EnableJpaRepositories(basePackages = {"com.platform.common", "com.platform.server"})
public class Application {
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
}