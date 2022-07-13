package br.com.santos.william.moviebattle;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@EnableFeignClients
@SpringBootApplication
@EnableDiscoveryClient
@EnableAutoConfiguration
public class MovieBattleApplication {

    public static void main(String[] args) {
        SpringApplication.run(MovieBattleApplication.class, args);
    }

}
