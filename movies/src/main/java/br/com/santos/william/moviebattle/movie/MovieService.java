package br.com.santos.william.moviebattle.movie;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.SendMessageRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MovieService {

    private AmazonSQS amazonSQS;
    private String queueUrl;
    private ObjectMapper mapper;

    public MovieService(
            AmazonSQS amazonSQS,
            @Value("${battle-movie.movie.catalog-queue}") String queueName,
            ObjectMapper mapper
    ) {
        this.amazonSQS = amazonSQS;
        //this.queueUrl = amazonSQS.getQueueUrl(queueName).getQueueUrl();
        this.mapper = mapper;
    }

    public void publish(MovieDto movie) {
        try {
            var request = new SendMessageRequest()
                    .withQueueUrl(queueUrl)
                    .withMessageBody(mapper.writeValueAsString(movie));
            amazonSQS.sendMessage(request);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }
}
