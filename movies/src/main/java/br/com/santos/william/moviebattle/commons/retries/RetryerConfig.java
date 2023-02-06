package br.com.santos.william.moviebattle.commons.retries;

import feign.Retryer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RetryerConfig {

    @Bean
    public Retryer retryer() {
        return new Retryer.Default();
    }

}
