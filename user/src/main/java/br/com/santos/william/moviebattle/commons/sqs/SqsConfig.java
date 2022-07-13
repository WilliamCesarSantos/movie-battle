package br.com.santos.william.moviebattle.commons.sqs;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.aws.messaging.config.annotation.EnableSqs;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

@EnableSqs
@Configuration
@EnableConfigurationProperties(SqsProperties.class)
public class SqsConfig {

    @Bean
    @Primary
    @ConditionalOnProperty(value = "aws.local-stack.enable", havingValue = "true", matchIfMissing = false)
    public AmazonSQS amazonSQSClient(SqsProperties properties) {
        return AmazonSQSClientBuilder.standard()
                .withEndpointConfiguration(
                        new AmazonSQSAsyncClientBuilder.EndpointConfiguration(
                                properties.getEndpoint(),
                                properties.getRegion()
                        )
                ).build();
    }

    @Bean
    @Primary
    @ConditionalOnProperty(value = "aws.local-stack.enable", havingValue = "true", matchIfMissing = false)
    public AmazonSQSAsync amazonSQSAsyncClient(SqsProperties properties) {
        return AmazonSQSAsyncClientBuilder.standard()
                .withEndpointConfiguration(
                        new AmazonSQSAsyncClientBuilder.EndpointConfiguration(
                                properties.getEndpoint(),
                                properties.getRegion()
                        )
                ).build();
    }

}
