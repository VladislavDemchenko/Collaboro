package org.demchenko;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.config.server.EnableConfigServer;


@SpringBootApplication
@EnableConfigServer
public class ConfigServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(ConfigServiceApp.class, args);
    }
}